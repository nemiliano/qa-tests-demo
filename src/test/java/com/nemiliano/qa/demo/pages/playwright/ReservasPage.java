package com.nemiliano.qa.demo.pages.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.nemiliano.qa.ui.playwright.BasePage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Página de reservas de la app local. */
public class ReservasPage extends BasePage {

  public ReservasPage(Page page) {
    super(page);
  }

  public ReservasPage openFor(String baseUrl, String huesped) {
    open(baseUrl + "/reservas?huesped=" + URLEncoder.encode(huesped, StandardCharsets.UTF_8));
    return this;
  }

  /** Fila de una reserva por su test id (data-testid, el atributo por defecto de Playwright). */
  public Locator row(long id) {
    return page.getByTestId("reserva-" + id);
  }
}
