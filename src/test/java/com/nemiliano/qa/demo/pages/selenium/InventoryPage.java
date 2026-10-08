package com.nemiliano.qa.demo.pages.selenium;

import com.nemiliano.qa.ui.selenium.BasePage;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

  private static final By HEADER_TITLE = By.cssSelector(".title");
  private static final By ADD_BACKPACK = By.id("add-to-cart-sauce-labs-backpack");
  private static final By REMOVE_BACKPACK = By.id("remove-sauce-labs-backpack");
  private static final By CART_BADGE = By.cssSelector(".shopping_cart_badge");

  public InventoryPage(WebDriver driver, Duration timeout) {
    super(driver, timeout);
  }

  public String headerTitle() {
    return getTextElement(HEADER_TITLE);
  }

  /**
   * Si la página aún no enganchó sus manejadores, el click se reintenta hasta que aparece "Remove".
   */
  public void addBackpackToCart() {
    clickUntilVisible(ADD_BACKPACK, REMOVE_BACKPACK);
  }

  public String cartCount() {
    return waitTextNotEmpty(CART_BADGE);
  }
}
