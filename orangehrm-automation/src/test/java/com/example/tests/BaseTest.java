package com.example.tests;

import com.example.config.ConfigReader;
import com.example.core.DriverFactory;
import com.example.pages.LoginPage;
import com.example.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

/**
 * Base class for all test classes.
 * This class provides common setup and teardown methods for tests.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected Logger logger;
    protected LoginPage loginPage;

    /**
     * Sets up the test class by initializing the WebDriver and logger.
     * @param browser The browser to use for the test (optional, from testng.xml)
     */
    @BeforeClass
    @Parameters({"browser"})
    public void setupClass(String browser) {
        logger = LogManager.getLogger(getClass());
        logger.info("Starting test class: {} in environment: {}", getClass().getSimpleName(), ConfigReader.getEnvironment());

        // Override browser if specified in testng.xml
        if (browser != null && !browser.isEmpty()) {
            System.setProperty("browser", browser);
            logger.info("Using browser from testng.xml: {}", browser);
        }

        driver = DriverFactory.getDriver();
    }

    /**
     * Sets up each test method by initializing the login page.
     */
    @BeforeMethod
    public void setupMethod() {
        logger.info("Starting test method");
        loginPage = new LoginPage(driver);
    }

    /**
     * Tears down each test method by taking a screenshot if the test failed.
     * @param result The test result
     */
    @AfterMethod
    public void teardownMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test failed: {}", result.getName());
            String screenshotName = getClass().getSimpleName() + "_" + result.getName() + "_failed.png";
            ScreenshotUtils.takeScreenshot(driver, screenshotName);
        }
        logger.info("Finished test method: {}", result.getName());
    }

    /**
     * Tears down the test class by quitting the WebDriver.
     */
    @AfterClass
    public void teardownClass() {
        logger.info("Finishing test class: {}", getClass().getSimpleName());
        DriverFactory.quitDriver();
    }
}