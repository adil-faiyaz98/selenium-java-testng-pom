package com.example.listeners;

import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry analyzer for failed tests.
 * This class determines whether a failed test should be retried.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);

    // Thread-local variables to support parallel test execution
    private static final ThreadLocal<Integer> retryCount = new ThreadLocal<>();
    private static int maxRetryCount = Constants.RETRY_COUNT;

    /**
     * Sets the maximum retry count.
     * @param count The maximum number of retries
     */
    public static void setMaxRetryCount(int count) {
        maxRetryCount = count;
    }

    /**
     * Gets the current retry count for the current thread.
     * @return The current retry count
     */
    private int getRetryCount() {
        Integer count = retryCount.get();
        return count == null ? 0 : count;
    }

    /**
     * Sets the retry count for the current thread.
     * @param count The retry count to set
     */
    private void setRetryCount(int count) {
        retryCount.set(count);
    }

    @Override
    public boolean retry(ITestResult result) {
        int currentRetryCount = getRetryCount();

        // Check if we should retry
        if (currentRetryCount < maxRetryCount) {
            currentRetryCount++;
            setRetryCount(currentRetryCount);

            String methodName = result.getMethod().getMethodName();
            String className = result.getTestClass().getRealClass().getSimpleName();

            logger.info("Retrying test: {}.{} (Attempt {} of {})",
                    className, methodName, currentRetryCount, maxRetryCount);

            // Clear the failed status for the test
            result.setStatus(ITestResult.SKIP);
            return true;
        }

        // Reset retry count for the next test
        setRetryCount(0);
        return false;
    }
}