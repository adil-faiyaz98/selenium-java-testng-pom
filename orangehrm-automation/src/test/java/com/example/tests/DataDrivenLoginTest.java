package com.example.tests;

import com.example.config.Constants;
import com.example.pages.DashboardPage;
import com.example.utils.DataUtils;
import com.example.utils.WaitUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Data-driven tests for the login functionality using JSON data.
 */
public class DataDrivenLoginTest extends BaseTest {

    /**
     * Data provider for valid login credentials from JSON file.
     * @return Iterator of Object arrays containing login data
     */
    @DataProvider(name = "validLoginData")
    public Iterator<Object[]> getValidLoginData() {
        List<Object[]> testData = new ArrayList<>();
        
        try {
            JSONObject jsonData = DataUtils.readDataFromJson("users.json");
            JSONArray validUsers = jsonData.getJSONArray("validUsers");
            
            for (int i = 0; i < validUsers.length(); i++) {
                JSONObject user = validUsers.getJSONObject(i);
                testData.add(new Object[] {
                    user.getString("username"),
                    user.getString("password"),
                    user.getString("role")
                });
            }
        } catch (Exception e) {
            logger.error("Error reading test data: {}", e.getMessage(), e);
        }
        
        return testData.iterator();
    }
    
    /**
     * Data provider for invalid login credentials from JSON file.
     * @return Iterator of Object arrays containing login data
     */
    @DataProvider(name = "invalidLoginData")
    public Iterator<Object[]> getInvalidLoginData() {
        List<Object[]> testData = new ArrayList<>();
        
        try {
            JSONObject jsonData = DataUtils.readDataFromJson("users.json");
            JSONArray invalidUsers = jsonData.getJSONArray("invalidUsers");
            
            for (int i = 0; i < invalidUsers.length(); i++) {
                JSONObject user = invalidUsers.getJSONObject(i);
                testData.add(new Object[] {
                    user.getString("username"),
                    user.getString("password"),
                    user.getString("errorMessage")
                });
            }
        } catch (Exception e) {
            logger.error("Error reading test data: {}", e.getMessage(), e);
        }
        
        return testData.iterator();
    }
    
    /**
     * Tests login with valid credentials from JSON data.
     * @param username The username
     * @param password The password
     * @param role The expected user role
     */
    @Test(dataProvider = "validLoginData", priority = 1, 
          description = "Verify successful login with valid credentials from JSON data")
    public void testValidLogin(String username, String password, String role) {
        logger.info("Testing valid login with username: {}, role: {}", username, role);
        
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
     * Tests login with invalid credentials from JSON data.
     * @param username The username
     * @param password The password
     * @param expectedError The expected error message
     */
    @Test(dataProvider = "invalidLoginData", priority = 2, 
          description = "Verify error message with invalid credentials from JSON data")
    public void testInvalidLogin(String username, String password, String expectedError) {
        logger.info("Testing invalid login with username: {}", username);
        
        // Perform login with invalid credentials
        loginPage.enterUsername(username)
                 .enterPassword(password)
                 .clickLoginButton();
        
        // Verify error message
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertEquals(errorMessage, expectedError, 
                "Expected error message not displayed. Actual message: " + errorMessage);
    }
    
    /**
     * Tests login with data from CSV file.
     */
    @Test(priority = 3, description = "Verify login with data from CSV file")
    public void testLoginWithCsvData() {
        logger.info("Testing login with CSV data");
        
        List<String[]> csvData = DataUtils.readDataFromCsv("login_data.csv");
        
        // Skip header row
        for (int i = 1; i < csvData.size(); i++) {
            String[] row = csvData.get(i);
            String username = row[0];
            String password = row[1];
            boolean shouldSucceed = Boolean.parseBoolean(row[2]);
            String expectedError = row[3];
            
            logger.info("Testing login with username: {}, shouldSucceed: {}", username, shouldSucceed);
            
            // Navigate to login page if not already there
            if (!driver.getCurrentUrl().contains("/auth/login")) {
                driver.get(Constants.BASE_URL);
            }
            
            // Perform login
            loginPage.enterUsername(username)
                     .enterPassword(password)
                     .clickLoginButton();
            
            if (shouldSucceed) {
                // Verify successful login
                WaitUtils.waitForUrlContains(driver, "/dashboard", Constants.DEFAULT_TIMEOUT);
                Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"), 
                        "Login should have succeeded but failed");
                
                // Logout for next test
                // TODO: Implement logout functionality
                driver.get(Constants.BASE_URL);
            } else {
                // Verify error message
                String errorMessage = loginPage.getErrorMessage();
                Assert.assertEquals(errorMessage, expectedError, 
                        "Expected error message not displayed. Actual message: " + errorMessage);
            }
        }
    }
}
