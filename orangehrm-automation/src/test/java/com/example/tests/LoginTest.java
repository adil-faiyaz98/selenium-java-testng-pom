package com.example.tests;

import com.example.config.ConfigReader;
import com.example.pages.DashboardPage;
import com.example.pages.LoginPage;
import com.example.utils.ScreenshotUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.By;

public class LoginTest extends BaseTest {

    @Test
    public void testSuccessfulLogin() {
        logger.info("Starting test: testSuccessfulLogin");
        try {
            LoginPage loginPage = new LoginPage(driver);
            String username = ConfigReader.getProperty("username");
            String password = ConfigReader.getProperty("password");

            loginPage.enterUsername(username);
            loginPage.enterPassword(password);
            loginPage.clickLoginButton();

            Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"), "Login failed: URL does not contain '/dashboard'");
            logger.info("Finished test: testSuccessfulLogin");

        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            ScreenshotUtils.takeScreenshot(driver, "LoginTest_testSuccessfulLogin_failed.png");
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidLogin() {
        logger.info("Starting test: testInvalidLogin");
        try {
            LoginPage loginPage = new LoginPage(driver);
            String invalidUsername = ConfigReader.getProperty("invalidUsername");
            String invalidPassword = ConfigReader.getProperty("invalidPassword");

            if (invalidUsername == null) invalidUsername = "invalidUser";
            if (invalidPassword == null) invalidPassword = "invalidPass";
            
            loginPage.enterUsername(invalidUsername);
            loginPage.enterPassword(invalidPassword);
            loginPage.clickLoginButton();

            Assert.assertTrue(driver.findElement(By.cssSelector(".oxd-alert-content-text")).getText().contains("Invalid credentials"));
            logger.info("Finished test: testInvalidLogin");
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            ScreenshotUtils.takeScreenshot(driver, "LoginTest_testInvalidLogin_failed.png");
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void testNoCredentialsLogin() {
        logger.info("Starting test: testNoCredentialsLogin");
        try {
            LoginPage loginPage = new LoginPage(driver);

            loginPage.clickLoginButton();

            Assert.assertTrue(driver.findElement(By.cssSelector(".oxd-alert-content-text")).getText().contains("Invalid credentials"));
            logger.info("Finished test: testNoCredentialsLogin");
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage(), e);
            ScreenshotUtils.takeScreenshot(driver, "LoginTest_testNoCredentialsLogin_failed.png");
            Assert.fail("Test failed: " + e.getMessage());
        }
    }
}