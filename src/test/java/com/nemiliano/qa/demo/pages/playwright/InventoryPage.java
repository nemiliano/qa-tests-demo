package com.nemiliano.qa.demo.pages.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.nemiliano.qa.ui.playwright.BasePage;

public class InventoryPage extends BasePage {

  private final Locator headerTitle = page.locator(".title");
  private final Locator addBackpack = page.locator("[data-test='add-to-cart-sauce-labs-backpack']");
  private final Locator cartBadge = page.locator(".shopping_cart_badge");

  public InventoryPage(Page page) {
    super(page);
  }

  public Locator headerTitle() {
    return headerTitle;
  }

  public void addBackpackToCart() {
    addBackpack.click();
  }

  public Locator cartBadge() {
    return cartBadge;
  }
}
