package com.nemiliano.qa.demo.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.nemiliano.qa.api.ApiClient;
import com.nemiliano.qa.db.DbClient;
import com.nemiliano.qa.demo.app.DemoApp;
import com.nemiliano.qa.demo.data.BookingBuilder;
import com.nemiliano.qa.junit.QaExtension;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Validación en base de datos: lo que hizo la API quedó persistido correctamente. */
@Tag("api")
@Tag("db")
@Tag("regression")
@ExtendWith(QaExtension.class)
class ReservaDbTest {

  private final DemoApp app = DemoApp.instance();
  private final ApiClient api = ApiClient.create(app.baseUrl());
  private final DbClient db = app.db();

  record ReservaRow(String huesped, int noches) {}

  @Test
  @DisplayName("Crear una reserva por API la persiste en la base")
  void reservationCreatedByApiIsPersisted() {
    // Arrange
    String guest = BookingBuilder.uniqueGuest();

    // Act
    Number id = api.post("/api/reservas", Map.of("huesped", guest, "noches", 2)).jsonPath("id");

    // Assert
    try {
      assertThat(
              db.queryOne(
                  "SELECT huesped, noches FROM reserva WHERE id = ?",
                  rs -> new ReservaRow(rs.getString("huesped"), rs.getInt("noches")),
                  id.longValue()))
          .contains(new ReservaRow(guest, 2));
    } finally {
      api.delete("/api/reservas/" + id);
    }
  }

  @Test
  @DisplayName("Borrar una reserva por API la elimina de la base")
  void reservationDeletedByApiIsRemoved() {
    // Arrange
    Number id =
        api.post("/api/reservas", Map.of("huesped", BookingBuilder.uniqueGuest(), "noches", 1))
            .jsonPath("id");

    // Act
    api.delete("/api/reservas/" + id);

    // Assert
    assertThat(db.query("SELECT id FROM reserva WHERE id = ?", rs -> rs.getLong(1), id.longValue()))
        .isEmpty();
  }

  @Test
  @DisplayName("La API rechaza una reserva sin noches")
  void invalidReservationIsRejected() {
    // Arrange
    String guest = BookingBuilder.uniqueGuest();

    // Act
    int status = api.post("/api/reservas", Map.of("huesped", guest, "noches", 0)).status();

    // Assert
    assertThat(status).isEqualTo(400);
  }
}
