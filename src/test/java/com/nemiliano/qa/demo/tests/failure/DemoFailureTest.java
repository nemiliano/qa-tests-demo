package com.nemiliano.qa.demo.tests.failure;

import static org.assertj.core.api.Assertions.assertThat;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.nemiliano.qa.demo.app.DemoApp;
import com.nemiliano.qa.junit.PlaywrightExtension;
import com.nemiliano.qa.junit.QaExtension;
import com.nemiliano.qa.junit.SeleniumExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * FALLAN A PROPÓSITO para practicar con las evidencias (screenshot, HTML, trace de Playwright y
 * reporte). Están excluidos de la ejecución normal. Para correrlos:
 *
 * <pre>mvnw test -DexcludedGroups=none -Dgroups=demo-failure</pre>
 */
@Tag("demo-failure")
class DemoFailureTest {

  @Nested
  @Tag("playwright")
  @ExtendWith({QaExtension.class, PlaywrightExtension.class})
  class WithPlaywright {

    @Test
    @DisplayName("Playwright: busca una reserva que no existe")
    void missingReservationFails(Page page) {
      page.navigate(DemoApp.instance().baseUrl() + "/reservas?huesped=nadie");

      PlaywrightAssertions.assertThat(page.getByTestId("reserva-999999"))
          .containsText("Reserva inexistente");
    }
  }

  @Nested
  @Tag("selenium")
  @ExtendWith({QaExtension.class, SeleniumExtension.class})
  class WithSelenium {

    @Test
    @DisplayName("Selenium: espera un título equivocado")
    void wrongTitleFails(WebDriver driver) {
      driver.get(DemoApp.instance().baseUrl() + "/reservas?huesped=nadie");

      assertThat(driver.findElement(By.tagName("h1")).getText()).isEqualTo("Listado de pedidos");
    }
  }
}
