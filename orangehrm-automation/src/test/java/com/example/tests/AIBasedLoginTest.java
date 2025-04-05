package com.example.tests;

import com.example.ai.ApplitoolsConnector;
import com.example.ai.ContentAnalyzer;
import com.example.ai.MLValidator;
import com.example.ai.TestGenerator;
import com.example.ai.TestimConnector;
import com.example.config.ConfigReader;
import com.example.config.Constants;
import com.example.pages.DashboardPage;
import com.example.pages.LoginPage;
import com.example.utils.PerformanceUtils;
import com.example.utils.PerformanceReporter;
import com.example.utils.VisualComparisonUtils;
import com.example.utils.VisualTestingReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * AI-based tests for the login functionality.
 * This class demonstrates the use of AI-based testing capabilities.
 */
public class AIBasedLoginTest extends BaseTest {

    private ApplitoolsConnector applitoolsConnector;
    private TestimConnector testimConnector;
    
    /**
     * Sets up the test class by initializing AI connectors.
     */
    @BeforeClass
    @Override
    public void setupClass(String browser) {
        super.setupClass(browser);
        
        // Initialize Applitools connector
        applitoolsConnector = new ApplitoolsConnector();
        applitoolsConnector.initialize();
        
        // Initialize Testim connector
        testimConnector = new TestimConnector();
        testimConnector.initialize();
        
        // Train ML validator with sample data
        MLValidator.train("username", Arrays.asList("Admin", "admin", "administrator", "user", "testuser"));
        MLValidator.train("password", Arrays.asList("admin123", "password123", "P@ssw0rd", "Test@123"));
    }
    
    /**
     * Tears down the test class by generating reports.
     */
    @AfterClass
    @Override
    public void teardownClass() {
        // Generate performance report
        PerformanceReporter.generateHtmlReport("LoginTest");
        
        // Generate visual testing report
        VisualTestingReporter.generateHtmlReport("LoginTest");
        
        super.teardownClass();
    }

    /**
     * Tests login with AI-based visual validation.
     */
    @Test(priority = 1, description = "Verify login page visual appearance with AI")
    public void testLoginPageVisualAppearance() {
        logger.info("Testing login page visual appearance with AI");
        
        // Start performance measurement
        PerformanceUtils.startPageLoadTimer("LoginPage");
        
        // Open Applitools test
        applitoolsConnector.openTest(driver, "OrangeHRM", "LoginPageVisualTest");
        
        // Check login page with Applitools
        boolean visualCheckPassed = applitoolsConnector.checkWindow(driver, "Login Page");
        
        // Capture performance metrics
        PerformanceUtils.stopPageLoadTimer("LoginPage");
        PerformanceUtils.capturePerformanceMetrics(driver, "LoginPage");
        PerformanceUtils.captureResourceTiming(driver, "LoginPage");
        
        // Save baseline for visual comparison if it doesn't exist
        String baselinePath = VisualComparisonUtils.saveBaseline(driver, "LoginPage");
        
        // Compare with baseline
        VisualComparisonUtils.ComparisonResult comparisonResult = 
                VisualComparisonUtils.compareWithBaseline(driver, "LoginPage");
        
        // Add result to report
        VisualTestingReporter.addResult(comparisonResult);
        
        // Close Applitools test
        applitoolsConnector.closeTest();
        
        // Assert visual check passed
        Assert.assertTrue(visualCheckPassed || comparisonResult.isPassed(), 
                "Visual validation failed for login page");
    }

    /**
     * Tests login form fields with ML-based validation.
     */
    @Test(priority = 2, description = "Verify login form fields with ML validation")
    public void testLoginFormFieldsWithMLValidation() {
        logger.info("Testing login form fields with ML validation");
        
        // Get username and password fields
        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        
        // Validate empty fields
        MLValidator.ValidationResult emptyUsernameResult = MLValidator.validateField(usernameField, "username");
        MLValidator.ValidationResult emptyPasswordResult = MLValidator.validateField(passwordField, "password");
        
        logger.info("Empty username validation: {}", emptyUsernameResult);
        logger.info("Empty password validation: {}", emptyPasswordResult);
        
        // Enter valid credentials
        usernameField.sendKeys(ConfigReader.getProperty("username", Constants.DEFAULT_USERNAME));
        passwordField.sendKeys(ConfigReader.getProperty("password", Constants.DEFAULT_PASSWORD));
        
        // Validate filled fields
        MLValidator.ValidationResult filledUsernameResult = MLValidator.validateField(usernameField, "username");
        MLValidator.ValidationResult filledPasswordResult = MLValidator.validateField(passwordField, "password");
        
        logger.info("Filled username validation: {}", filledUsernameResult);
        logger.info("Filled password validation: {}", filledPasswordResult);
        
        // Assert field validation
        Assert.assertTrue(filledUsernameResult.isValid(), 
                "Username field validation failed: " + filledUsernameResult.getMessage());
        Assert.assertTrue(filledPasswordResult.isValid(), 
                "Password field validation failed: " + filledPasswordResult.getMessage());
    }

    /**
     * Tests login page content with NLP analysis.
     */
    @Test(priority = 3, description = "Verify login page content with NLP analysis")
    public void testLoginPageContentWithNLPAnalysis() {
        logger.info("Testing login page content with NLP analysis");
        
        // Analyze page content relevance to "login"
        ContentAnalyzer.PageAnalysisResult pageAnalysis = 
                ContentAnalyzer.analyzePage(driver, "login");
        
        logger.info("Page analysis result: {}", pageAnalysis);
        
        // Get login button text
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        String loginButtonText = loginButton.getText();
        
        // Analyze login button text sentiment
        ContentAnalyzer.SentimentResult buttonSentiment = 
                ContentAnalyzer.analyzeSentiment(loginButtonText);
        
        logger.info("Login button sentiment: {}", buttonSentiment);
        
        // Assert content analysis
        Assert.assertTrue(pageAnalysis.getScore() > 0.5, 
                "Page content is not relevant to login: " + pageAnalysis.getScore());
    }

    /**
     * Tests successful login with AI-assisted validation.
     */
    @Test(priority = 4, description = "Verify successful login with AI-assisted validation")
    public void testSuccessfulLoginWithAIValidation() {
        logger.info("Testing successful login with AI-assisted validation");
        
        // Start Testim test
        testimConnector.startTest(driver, "SuccessfulLoginTest");
        
        // Start performance measurement
        PerformanceUtils.startPageLoadTimer("LoginProcess");
        
        // Execute login steps with Testim
        testimConnector.executeStep(driver, "EnterUsername", "type", "input[name='username']");
        loginPage.enterUsername(ConfigReader.getProperty("username", Constants.DEFAULT_USERNAME));
        
        testimConnector.executeStep(driver, "EnterPassword", "type", "input[name='password']");
        loginPage.enterPassword(ConfigReader.getProperty("password", Constants.DEFAULT_PASSWORD));
        
        testimConnector.executeStep(driver, "ClickLoginButton", "click", "button[type='submit']");
        DashboardPage dashboardPage = loginPage.clickLoginButton();
        
        // Stop performance measurement
        long loginTime = PerformanceUtils.stopPageLoadTimer("LoginProcess");
        
        // Capture performance metrics for dashboard page
        PerformanceUtils.capturePerformanceMetrics(driver, "DashboardPage");
        PerformanceUtils.captureResourceTiming(driver, "DashboardPage");
        
        // Validate dashboard page with Applitools
        applitoolsConnector.openTest(driver, "OrangeHRM", "DashboardPageVisualTest");
        boolean dashboardVisualCheckPassed = applitoolsConnector.checkWindow(driver, "Dashboard Page");
        applitoolsConnector.closeTest();
        
        // End Testim test
        boolean testimTestPassed = testimConnector.endTest();
        
        // Assert login was successful
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"), 
                "Login failed: URL does not contain '/dashboard'");
        Assert.assertTrue(dashboardPage.isAdminMenuPresent(), 
                "Dashboard page not loaded correctly: Admin menu not present");
        
        // Assert performance
        Assert.assertTrue(loginTime < 5000, 
                "Login process took too long: " + loginTime + "ms");
        
        // Assert AI validations
        Assert.assertTrue(dashboardVisualCheckPassed, 
                "Dashboard page visual validation failed");
        Assert.assertTrue(testimTestPassed, 
                "Testim test validation failed");
    }

    /**
     * Generates test cases for the login page.
     */
    @Test(priority = 5, description = "Generate test cases for login page")
    public void testGenerateLoginPageTestCases() {
        logger.info("Generating test cases for login page");
        
        // Navigate back to login page
        driver.get(Constants.BASE_URL);
        
        // Generate test cases
        List<TestGenerator.TestCase> testCases = TestGenerator.generateTestCases(driver, "Login");
        
        // Log generated test cases
        for (TestGenerator.TestCase testCase : testCases) {
            logger.info("Generated test case: {}", testCase);
        }
        
        // Assert test cases were generated
        Assert.assertFalse(testCases.isEmpty(), 
                "No test cases were generated for login page");
    }
}
