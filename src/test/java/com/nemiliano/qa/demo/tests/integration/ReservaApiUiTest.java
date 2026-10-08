package com.nemiliano.qa.demo.tests.integration;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.nemiliano.qa.api.ApiClient;
import com.nemiliano.qa.demo.app.DemoApp;
import com.nemiliano.qa.demo.data.BookingBuilder;
import com.nemiliano.qa.demo.pages.playwright.ReservasPage;
import com.nemiliano.qa.junit.PlaywrightExtension;
import com.nemiliano.qa.junit.QaExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * API + UI: el dato se prepara por API (rápido y estable) y se valida por UI (lo que ve el
 * usuario). Es mucho más rápido y menos frágil que crear el dato navegando por la pantalla.
 */
@Tag("api")
@Tag("ui")
@Tag("playwright")
@Tag("regression")
@ExtendWith({QaExtension.class, PlaywrightExtension.class})
class ReservaApiUiTest {

  private final String baseUrl = DemoApp.instance().baseUrl();
  private final ApiClient api = ApiClient.create(baseUrl);
  private final List<Long> created = new ArrayList<>();

  @AfterEach
  void cleanUp() {
    created.forEach(id -> api.delete("/api/reservas/" + id));
  }

  @Test
  @DisplayName("Una reserva creada por API aparece en la página")
  void reservationCreatedByApiShowsInPage(Page page) {
    // Arrange: dato por API
    String guest = BookingBuilder.uniqueGuest();
    long id = createReservation(guest, 3);

    // Act: el usuario abre la página
    ReservasPage reservas = new ReservasPage(page).openFor(baseUrl, guest);

    // Assert: lo que ve el usuario
    PlaywrightAssertions.assertThat(reservas.row(id)).containsText(guest);
  }

  private long createReservation(String guest, int nights) {
    long id =
        ((Number)
                api.post("/api/reservas", Map.of("huesped", guest, "noches", nights))
                    .jsonPath("id"))
            .longValue();
    created.add(id);
    return id;
  }
}
