package com.example.listeners;

import com.example.config.ConfigReader;
import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG listener for retry functionality.
 * This class automatically applies the RetryAnalyzer to all test methods.
 */
public class RetryListener implements IAnnotationTransformer {

    private static final Logger logger = LogManager.getLogger(RetryListener.class);

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        IRetryAnalyzer retryAnalyzer = annotation.getRetryAnalyzer();
        
        // If retry analyzer is not set, set it
        if (retryAnalyzer == null) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
            logger.debug("Setting retry analyzer for method: {}", testMethod.getName());
        }
        
        // Get retry count from configuration or use default
        String retryCountStr = ConfigReader.getProperty("retryCount");
        if (retryCountStr != null) {
            try {
                int retryCount = Integer.parseInt(retryCountStr);
                RetryAnalyzer.setMaxRetryCount(retryCount);
                logger.info("Set retry count to {} from configuration", retryCount);
            } catch (NumberFormatException e) {
                logger.warn("Invalid retry count in configuration: {}, using default: {}", 
                        retryCountStr, Constants.RETRY_COUNT);
                RetryAnalyzer.setMaxRetryCount(Constants.RETRY_COUNT);
            }
        } else {
            logger.info("Using default retry count: {}", Constants.RETRY_COUNT);
            RetryAnalyzer.setMaxRetryCount(Constants.RETRY_COUNT);
        }
    }
}
