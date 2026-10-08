package com.nemiliano.qa.demo.tests.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.nemiliano.qa.core.config.QaConfig;
import com.nemiliano.qa.demo.pages.playwright.InventoryPage;
import com.nemiliano.qa.demo.pages.playwright.LoginPage;
import com.nemiliano.qa.junit.PlaywrightExtension;
import com.nemiliano.qa.junit.QaExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Escenario 1 con PLAYWRIGHT. Compará con {@link ShoppingSeleniumTest}: mismo flujo, menos código.
 */
@Tag("ui")
@Tag("playwright")
@Tag("smoke")
@ExtendWith({QaExtension.class, PlaywrightExtension.class})
class ShoppingPlaywrightTest {

  @Test
  @DisplayName("Playwright: un usuario agrega la mochila al carrito")
  void userAddsBackpackToCart(Page page, QaConfig config) {
    // Arrange
    LoginPage login = new LoginPage(page);
    login.open(config.baseUrl());

    // Act
    InventoryPage inventory = login.loginAs("standard_user", "secret_sauce");
    inventory.addBackpackToCart();

    // Assert (web-first: reintenta solo hasta que se cumple o vence el timeout)
    PlaywrightAssertions.assertThat(inventory.cartBadge()).hasText("1");
  }
}
