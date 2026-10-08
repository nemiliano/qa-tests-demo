package com.nemiliano.qa.demo.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemiliano.qa.db.DbClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Mini aplicación local que hace de "sistema bajo prueba" para los tests que combinan API, UI y DB
 * sin depender de Internet: una API REST, una página HTML y una base de datos H2.
 *
 * <ul>
 *   <li>{@code POST /api/reservas} crea una reserva y devuelve su id
 *   <li>{@code GET /api/reservas/{id}} / {@code DELETE /api/reservas/{id}}
 *   <li>{@code GET /reservas?huesped=...} página con la tabla de reservas (filtrable)
 * </ul>
 *
 * Se levanta una sola vez por ejecución (singleton) y es seguro usarla desde tests en paralelo.
 */
public final class DemoApp {

  private static final ObjectMapper JSON = new ObjectMapper();
  private static DemoApp instance;

  private final HttpServer server;
  private final DbClient db;

  private DemoApp() throws IOException {
    db = DbClient.create("jdbc:h2:mem:demoapp;DB_CLOSE_DELAY=-1", "sa", "");
    db.update(
        "CREATE TABLE reserva ("
            + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
            + "huesped VARCHAR(100) NOT NULL, "
            + "noches INT NOT NULL)");

    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.setExecutor(Executors.newFixedThreadPool(4, r -> daemon(new Thread(r, "demo-app"))));
    server.createContext("/api/reservas", this::handleApi);
    server.createContext("/reservas", this::handlePage);
    server.start();
  }

  public static synchronized DemoApp instance() {
    if (instance == null) {
      try {
        instance = new DemoApp();
      } catch (IOException e) {
        throw new IllegalStateException("No se pudo iniciar la app local de la demo", e);
      }
      Runtime.getRuntime().addShutdownHook(new Thread(instance::stop));
    }
    return instance;
  }

  public String baseUrl() {
    return "http://127.0.0.1:" + server.getAddress().getPort();
  }

  /** Acceso directo a la base de la app, para las validaciones de datos. */
  public DbClient db() {
    return db;
  }

  private void stop() {
    server.stop(0);
    db.close();
  }

  private void handleApi(HttpExchange ex) throws IOException {
    String path = ex.getRequestURI().getPath();
    String method = ex.getRequestMethod();
    String idPart = path.substring("/api/reservas".length());
    try {
      if (method.equals("POST") && idPart.isEmpty()) {
        create(ex);
      } else if (method.equals("GET") && idPart.startsWith("/")) {
        read(ex, Long.parseLong(idPart.substring(1)));
      } else if (method.equals("DELETE") && idPart.startsWith("/")) {
        delete(ex, Long.parseLong(idPart.substring(1)));
      } else {
        send(ex, 404, "application/json", "{\"error\":\"no existe\"}");
      }
    } catch (NumberFormatException e) {
      send(ex, 400, "application/json", "{\"error\":\"id inválido\"}");
    }
  }

  private void create(HttpExchange ex) throws IOException {
    JsonNode body = JSON.readTree(ex.getRequestBody());
    String huesped = body.path("huesped").asText("");
    int noches = body.path("noches").asInt(0);
    if (huesped.isBlank() || noches < 1) {
      send(ex, 400, "application/json", "{\"error\":\"huesped y noches (>=1) son obligatorios\"}");
      return;
    }
    long id =
        db.queryOne(
                "SELECT id FROM FINAL TABLE (INSERT INTO reserva (huesped, noches) VALUES (?, ?))",
                rs -> rs.getLong(1),
                huesped,
                noches)
            .orElseThrow();
    send(ex, 201, "application/json", "{\"id\":" + id + "}");
  }

  private void read(HttpExchange ex, long id) throws IOException {
    var row =
        db.queryOne(
            "SELECT huesped, noches FROM reserva WHERE id = ?",
            rs -> new String[] {rs.getString(1), String.valueOf(rs.getInt(2))},
            id);
    if (row.isEmpty()) {
      send(ex, 404, "application/json", "{\"error\":\"no existe\"}");
      return;
    }
    send(
        ex,
        200,
        "application/json",
        JSON.writeValueAsString(
            java.util.Map.of(
                "id", id, "huesped", row.get()[0], "noches", Integer.parseInt(row.get()[1]))));
  }

  private void delete(HttpExchange ex, long id) throws IOException {
    int removed = db.update("DELETE FROM reserva WHERE id = ?", id);
    send(ex, removed > 0 ? 204 : 404, "application/json", removed > 0 ? "" : "{}");
  }

  private void handlePage(HttpExchange ex) throws IOException {
    String query = ex.getRequestURI().getQuery();
    String filter = "";
    if (query != null && query.startsWith("huesped=")) {
      filter =
          java.net.URLDecoder.decode(query.substring("huesped=".length()), StandardCharsets.UTF_8);
    }
    List<String[]> rows =
        db.query(
            "SELECT id, huesped, noches FROM reserva WHERE huesped LIKE ? ORDER BY id",
            rs -> new String[] {rs.getString(1), rs.getString(2), rs.getString(3)},
            "%" + filter + "%");

    StringBuilder html =
        new StringBuilder(
            "<!doctype html><html><head><meta charset='utf-8'><title>Reservas</title></head>"
                + "<body><h1>Reservas</h1><table id='reservas'><thead><tr><th>Huésped</th>"
                + "<th>Noches</th></tr></thead><tbody>");
    for (String[] row : rows) {
      html.append("<tr data-testid='reserva-")
          .append(row[0])
          .append("'><td class='huesped'>")
          .append(escape(row[1]))
          .append("</td><td class='noches'>")
          .append(row[2])
          .append("</td></tr>");
    }
    html.append("</tbody></table></body></html>");
    send(ex, 200, "text/html; charset=utf-8", html.toString());
  }

  private static String escape(String text) {
    return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  private static void send(HttpExchange ex, int status, String type, String body)
      throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    ex.getResponseHeaders().add("Content-Type", type);
    ex.sendResponseHeaders(status, status == 204 ? -1 : bytes.length);
    if (status != 204) {
      ex.getResponseBody().write(bytes);
    }
    ex.close();
  }

  private static Thread daemon(Thread thread) {
    thread.setDaemon(true);
    return thread;
  }
}
