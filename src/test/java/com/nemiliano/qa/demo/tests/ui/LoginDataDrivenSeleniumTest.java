package com.nemiliano.qa.demo.tests.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.nemiliano.qa.core.config.QaConfig;
import com.nemiliano.qa.data.CsvTestData;
import com.nemiliano.qa.demo.data.LoginData;
import com.nemiliano.qa.demo.pages.selenium.LoginPage;
import com.nemiliano.qa.junit.QaExtension;
import com.nemiliano.qa.junit.SeleniumExtension;
import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.openqa.selenium.WebDriver;

/**
 * Igual que {@link LoginDataDrivenTest} pero con SELENIUM y el mismo CSV: un escenario, un caso de
 * prueba por fila. Comparar ambos muestra la diferencia entre las dos herramientas con los mismos
 * datos y el mismo resultado esperado.
 */
@Tag("ui")
@Tag("selenium")
@Tag("regression")
@ExtendWith({QaExtension.class, SeleniumExtension.class})
class LoginDataDrivenSeleniumTest {

  @ParameterizedTest(name = "{0}")
  @CsvTestData(file = "data/login.csv", type = LoginData.class)
  void loginShowsExpectedOutcome(LoginData data, WebDriver driver, QaConfig config) {
    // Arrange
    LoginPage login = new LoginPage(driver, Duration.ofSeconds(config.timeoutSeconds()));
    login.open(config.baseUrl());

    // Act
    login.loginAs(data.usuario(), data.clave());

    // Assert: el título del inventario o el mensaje de error, según la fila
    assertThat(login.outcomeText()).contains(data.resultadoEsperado());
  }
}
