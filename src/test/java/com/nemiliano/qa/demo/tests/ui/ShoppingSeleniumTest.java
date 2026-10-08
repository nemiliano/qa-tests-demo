package com.nemiliano.qa.demo.tests.ui;

import static org.assertj.core.api.Assertions.assertThat;

import com.nemiliano.qa.core.config.QaConfig;
import com.nemiliano.qa.demo.pages.selenium.InventoryPage;
import com.nemiliano.qa.demo.pages.selenium.LoginPage;
import com.nemiliano.qa.junit.QaExtension;
import com.nemiliano.qa.junit.SeleniumExtension;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

/** Escenario 1 con SELENIUM. El mismo escenario está en {@link ShoppingPlaywrightTest}. */
@Tag("ui")
@Tag("selenium")
@Tag("smoke")
@ExtendWith({QaExtension.class, SeleniumExtension.class})
class ShoppingSeleniumTest {

  @Test
  @DisplayName("Selenium: un usuario agrega la mochila al carrito")
  void userAddsBackpackToCart(WebDriver driver, QaConfig config) {
    // Arrange
    Duration timeout = Duration.ofSeconds(config.timeoutSeconds());
    LoginPage login = new LoginPage(driver, timeout);
    login.open(config.baseUrl());

    // Act
    InventoryPage inventory = login.loginAs("standard_user", "secret_sauce");
    inventory.addBackpackToCart();

    // Assert
    assertThat(inventory.cartCount()).isEqualTo("1");
  }
}
