package com.example.pages;

import com.example.config.Constants;
import com.example.utils.JavaScriptExecutorUtil;
import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object for the Login page.
 * This class provides methods to interact with the login page elements.
 */
public class LoginPage {
    private WebDriver driver;

    // Locators
    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By errorMessage = By.cssSelector(".oxd-alert-content-text");
    private final By forgotPasswordLink = By.cssSelector(".orangehrm-login-forgot");
    private final By logoImage = By.cssSelector(".orangehrm-login-branding img");

    /**
     * Constructor for the LoginPage.
     * @param driver The WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Enters the username in the username field.
     * @param username The username to enter
     * @return The LoginPage instance for method chaining
     */
    public LoginPage enterUsername(String username) {
        WebElement usernameElement = WaitUtils.waitForElementVisible(driver, usernameField, Constants.DEFAULT_TIMEOUT);
        usernameElement.clear();
        usernameElement.sendKeys(username);
        return this;
    }

    /**
     * Enters the password in the password field.
     * @param password The password to enter
     * @return The LoginPage instance for method chaining
     */
    public LoginPage enterPassword(String password) {
        WebElement passwordElement = WaitUtils.waitForElementVisible(driver, passwordField, Constants.DEFAULT_TIMEOUT);
        passwordElement.clear();
        passwordElement.sendKeys(password);
        return this;
    }

    /**
     * Clicks the login button.
     * @return The DashboardPage instance if login is successful
     */
    public DashboardPage clickLoginButton() {
        WebElement loginButtonElement = WaitUtils.waitForElementClickable(driver, loginButton, Constants.DEFAULT_TIMEOUT);
        // Highlight the button for better visibility in reports
        JavaScriptExecutorUtil.highlightElement(driver, loginButtonElement);
        loginButtonElement.click();
        return new DashboardPage(driver);
    }

    /**
     * Performs login with the specified credentials.
     * @param username The username
     * @param password The password
     * @return The DashboardPage instance if login is successful
     */
    public DashboardPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginButton();
    }

    /**
     * Gets the error message text if login fails.
     * @return The error message text
     */
    public String getErrorMessage() {
        WebElement errorElement = WaitUtils.waitForElementVisible(driver, errorMessage, Constants.DEFAULT_TIMEOUT);
        return errorElement.getText();
    }

    /**
     * Checks if the login page is displayed.
     * @return true if the login page is displayed, false otherwise
     */
    public boolean isLoginPageDisplayed() {
        return WaitUtils.waitForElementVisible(driver, logoImage, Constants.DEFAULT_TIMEOUT).isDisplayed();
    }

    /**
     * Clicks the forgot password link.
     */
    public void clickForgotPasswordLink() {
        WebElement forgotPasswordElement = WaitUtils.waitForElementClickable(driver, forgotPasswordLink, Constants.DEFAULT_TIMEOUT);
        forgotPasswordElement.click();
    }
}