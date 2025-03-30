package com.example.listeners;

import com.example.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: " + result.getMethod().getQualifiedName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test succeeded: " + result.getMethod().getQualifiedName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test failed: " + result.getMethod().getQualifiedName(), result.getThrowable());

        // Take screenshot
        Object currentClass = result.getInstance();
        WebDriver driver = null;
        try {
            driver = (WebDriver) currentClass.getClass().getDeclaredField("driver").get(currentClass);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("Could not access WebDriver instance for screenshot: " + e.getMessage(), e);
        }

        if (driver != null) {
            String screenshotName = result.getTestClass().getRealClass().getSimpleName() + "_" + result.getMethod().getMethodName() + "_failed.png";
            ScreenshotUtils.takeScreenshot(driver, screenshotName);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test skipped: " + result.getMethod().getQualifiedName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Test failed but within success percentage: " + result.getMethod().getQualifiedName());
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test suite started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test suite finished: " + context.getName());
    }
}