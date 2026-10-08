package com.nemiliano.qa.demo.tests.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.nemiliano.qa.core.config.QaConfig;
import com.nemiliano.qa.data.CsvTestData;
import com.nemiliano.qa.demo.data.LoginData;
import com.nemiliano.qa.demo.pages.playwright.LoginPage;
import com.nemiliano.qa.junit.PlaywrightExtension;
import com.nemiliano.qa.junit.QaExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Un método (escenario "login") que se ejecuta una vez por fila del CSV (casos de prueba). El
 * nombre de cada ejecución es el "Caso de prueba"; en Allure se ven los datos y el resultado
 * esperado.
 */
@Tag("ui")
@Tag("playwright")
@Tag("regression")
@ExtendWith({QaExtension.class, PlaywrightExtension.class})
class LoginDataDrivenTest {

  @ParameterizedTest(name = "{0}")
  @CsvTestData(file = "data/login.csv", type = LoginData.class)
  void loginShowsExpectedOutcome(LoginData data, Page page, QaConfig config) {
    // Arrange
    LoginPage login = new LoginPage(page);
    login.open(config.baseUrl());

    // Act
    login.loginAs(data.usuario(), data.clave());

    // Assert: el título del inventario o el mensaje de error, según la fila
    PlaywrightAssertions.assertThat(login.outcome()).containsText(data.resultadoEsperado());
  }
}
