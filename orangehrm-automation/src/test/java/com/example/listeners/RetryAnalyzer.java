package com.example.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;
    private int maxRetryCount = 2; // You can adjust the maximum number of retries

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.info("Retrying test: " + result.getMethod().getMethodName() + " (Attempt " + retryCount + " of " + maxRetryCount + ")");
            return true;
        }
        return false;
    }
}