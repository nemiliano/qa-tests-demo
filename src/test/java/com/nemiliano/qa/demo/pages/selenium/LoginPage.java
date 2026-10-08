package com.nemiliano.qa.demo.pages.selenium;

import com.nemiliano.qa.ui.selenium.BasePage;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Login de saucedemo con Selenium: cada acción espera su condición (sin Thread.sleep). */
public class LoginPage extends BasePage {

  private static final By USERNAME = By.id("user-name");
  private static final By PASSWORD = By.id("password");
  private static final By LOGIN = By.id("login-button");
  private static final By ERROR = By.cssSelector("[data-test='error']");
  private static final By OUTCOME = By.cssSelector(".title, [data-test='error']");

  private final Duration timeout;

  public LoginPage(WebDriver driver, Duration timeout) {
    super(driver, timeout);
    this.timeout = timeout;
  }

  public InventoryPage loginAs(String user, String password) {
    type(USERNAME, user);
    type(PASSWORD, password);
    click(LOGIN);
    return new InventoryPage(driver, timeout);
  }

  public String errorMessage() {
    return waitTextNotEmpty(ERROR);
  }

  /** El título del inventario o el mensaje de error, lo que aparezca primero. */
  public String outcomeText() {
    return waitTextNotEmpty(OUTCOME);
  }
}
