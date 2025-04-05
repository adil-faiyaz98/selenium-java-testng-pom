package com.example.listeners;

import com.example.config.Constants;
import com.example.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG listener for test execution events.
 * This class provides logging, screenshot capture, and browser log capture for test events.
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: {}", result.getMethod().getQualifiedName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test succeeded: {}", result.getMethod().getQualifiedName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        logger.error("Test failed: {}.{}", className, testName, result.getThrowable());

        // Get WebDriver instance
        WebDriver driver = getDriverFromResult(result);
        if (driver == null) {
            logger.error("Could not access WebDriver instance for capturing failure evidence");
            return;
        }

        // Create directory for failure evidence
        String timestamp = DATE_FORMAT.format(new Date());
        Path evidencePath = Paths.get(Constants.SCREENSHOT_PATH, className);
        try {
            Files.createDirectories(evidencePath);
        } catch (IOException e) {
            logger.error("Could not create directory for failure evidence: {}", evidencePath, e);
            return;
        }

        // Take screenshot
        String screenshotName = className + "_" + testName + "_" + timestamp + ".png";
        ScreenshotUtils.takeScreenshot(driver, screenshotName);

        // Capture browser logs
        captureBrowserLogs(driver, evidencePath, className, testName, timestamp);

        // Capture page source
        capturePageSource(driver, evidencePath, className, testName, timestamp);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test skipped: {}", result.getMethod().getQualifiedName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Test failed but within success percentage: {}", result.getMethod().getQualifiedName());
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test suite started: {}", context.getName());

        // Create directories for test artifacts
        try {
            Files.createDirectories(Paths.get(Constants.SCREENSHOT_PATH));
            Files.createDirectories(Paths.get(Constants.REPORT_PATH));
        } catch (IOException e) {
            logger.error("Could not create directories for test artifacts", e);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;

        logger.info("Test suite finished: {}", context.getName());
        logger.info("Test results: Total={}, Passed={}, Failed={}, Skipped={}",
                total, passed, failed, skipped);
    }

    /**
     * Gets the WebDriver instance from the test result.
     * @param result The test result
     * @return The WebDriver instance or null if not found
     */
    private WebDriver getDriverFromResult(ITestResult result) {
        Object currentClass = result.getInstance();
        try {
            return (WebDriver) currentClass.getClass().getDeclaredField("driver").get(currentClass);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("Could not access WebDriver instance: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Captures browser logs and saves them to a file.
     * @param driver The WebDriver instance
     * @param evidencePath The path to save the logs
     * @param className The test class name
     * @param testName The test method name
     * @param timestamp The timestamp
     */
    private void captureBrowserLogs(WebDriver driver, Path evidencePath, String className, String testName, String timestamp) {
        try {
            LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
            if (logs != null && logs.getAll().size() > 0) {
                Path logFile = evidencePath.resolve(className + "_" + testName + "_" + timestamp + "_browser.log");
                try (FileWriter writer = new FileWriter(logFile.toFile())) {
                    writer.write("Browser logs for " + className + "." + testName + "\n\n");
                    for (LogEntry entry : logs.getAll()) {
                        writer.write(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage() + "\n");
                    }
                }
                logger.info("Browser logs saved to: {}", logFile);
            }
        } catch (Exception e) {
            logger.error("Failed to capture browser logs: {}", e.getMessage(), e);
        }
    }

    /**
     * Captures the page source and saves it to a file.
     * @param driver The WebDriver instance
     * @param evidencePath The path to save the page source
     * @param className The test class name
     * @param testName The test method name
     * @param timestamp The timestamp
     */
    private void capturePageSource(WebDriver driver, Path evidencePath, String className, String testName, String timestamp) {
        try {
            String pageSource = driver.getPageSource();
            Path sourceFile = evidencePath.resolve(className + "_" + testName + "_" + timestamp + "_source.html");
            Files.write(sourceFile, pageSource.getBytes());
            logger.info("Page source saved to: {}", sourceFile);
        } catch (Exception e) {
            logger.error("Failed to capture page source: {}", e.getMessage(), e);
        }
    }
}