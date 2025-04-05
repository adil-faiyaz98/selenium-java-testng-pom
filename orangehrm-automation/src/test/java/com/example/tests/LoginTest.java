package com.example.tests;

import com.example.config.ConfigReader;
import com.example.config.Constants;
import com.example.pages.DashboardPage;
import com.example.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for the login functionality.
 */
public class LoginTest extends BaseTest {

    /**
     * Tests successful login with valid credentials.
     */
    @Test(priority = 1, description = "Verify successful login with valid credentials")
    public void testSuccessfulLogin() {
        logger.info("Testing successful login");

        // Get credentials from configuration
        String username = ConfigReader.getProperty("username", Constants.DEFAULT_USERNAME);
        String password = ConfigReader.getProperty("password", Constants.DEFAULT_PASSWORD);

        // Perform login
        DashboardPage dashboardPage = loginPage.login(username, password);

        // Verify login was successful
        WaitUtils.waitForUrlContains(driver, "/dashboard", Constants.DEFAULT_TIMEOUT);
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Login failed: URL does not contain '/dashboard'");
        Assert.assertTrue(dashboardPage.isAdminMenuPresent(),
                "Dashboard page not loaded correctly: Admin menu not present");
    }

    /**
     * Tests login with invalid credentials.
     */
    @Test(priority = 2, description = "Verify error message with invalid credentials")
    public void testInvalidLogin() {
        logger.info("Testing invalid login");

        // Get invalid credentials from configuration or use defaults
        String invalidUsername = ConfigReader.getProperty("invalidUsername", "invalidUser");
        String invalidPassword = ConfigReader.getProperty("invalidPassword", "invalidPass");

        // Perform login with invalid credentials
        loginPage.enterUsername(invalidUsername)
                 .enterPassword(invalidPassword)
                 .clickLoginButton();

        // Verify error message
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Invalid credentials"),
                "Expected error message not displayed. Actual message: " + errorMessage);
    }

    /**
     * Tests login with no credentials.
     */
    @Test(priority = 3, description = "Verify error message with no credentials")
    public void testNoCredentialsLogin() {
        logger.info("Testing login with no credentials");

        // Click login without entering credentials
        loginPage.clickLoginButton();

        // Verify error message
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Invalid credentials"),
                "Expected error message not displayed. Actual message: " + errorMessage);
    }

    /**
     * Data provider for login tests with different credentials.
     * @return Array of test data
     */
    @DataProvider(name = "loginCredentials")
    public Object[][] provideLoginCredentials() {
        return new Object[][] {
            {Constants.DEFAULT_USERNAME, Constants.DEFAULT_PASSWORD, true, null},  // Valid credentials
            {"invalidUser", "invalidPass", false, "Invalid credentials"},         // Invalid credentials
            {"", "", false, "Invalid credentials"}                                // Empty credentials
        };
    }

    /**
     * Tests login with different credentials using data provider.
     * @param username The username
     * @param password The password
     * @param shouldSucceed Whether the login should succeed
     * @param expectedError The expected error message if login should fail
     */
    @Test(dataProvider = "loginCredentials", priority = 4, description = "Verify login with different credentials")
    public void testLoginWithDifferentCredentials(String username, String password, boolean shouldSucceed, String expectedError) {
        logger.info("Testing login with credentials: {} / {}", username, password);

        // Perform login
        loginPage.enterUsername(username)
                 .enterPassword(password)
                 .clickLoginButton();

        if (shouldSucceed) {
            // Verify successful login
            WaitUtils.waitForUrlContains(driver, "/dashboard", Constants.DEFAULT_TIMEOUT);
            Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                    "Login should have succeeded but failed");
        } else {
            // Verify error message
            String errorMessage = loginPage.getErrorMessage();
            Assert.assertEquals(errorMessage, expectedError,
                    "Expected error message not displayed");
        }
    }
}