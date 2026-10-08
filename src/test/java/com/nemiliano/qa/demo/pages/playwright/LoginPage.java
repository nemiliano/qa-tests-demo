package com.nemiliano.qa.demo.pages.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.nemiliano.qa.ui.playwright.BasePage;

/**
 * Login de saucedemo con Playwright. Los locators son perezosos y cada acción espera sola
 * (auto-wait): no hay equivalentes de clickable() ni visible(). Se prefieren locators por rol y
 * texto visible.
 */
public class LoginPage extends BasePage {

  private final Locator username = page.getByPlaceholder("Username");
  private final Locator password = page.getByPlaceholder("Password");
  private final Locator loginButton =
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
  private final Locator error = page.locator("[data-test='error']");

  public LoginPage(Page page) {
    super(page);
  }

  public InventoryPage loginAs(String user, String pass) {
    username.fill(user);
    password.fill(pass);
    loginButton.click();
    return new InventoryPage(page);
  }

  public Locator error() {
    return error;
  }

  /** El título del inventario o el mensaje de error, lo que aparezca. */
  public Locator outcome() {
    return page.locator(".title, [data-test='error']");
  }
}
