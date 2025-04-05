package com.example.config;

/**
 * Constants used throughout the automation framework.
 * This interface provides centralized access to common constants.
 */
public interface Constants {
    // URLs
    String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    // Timeouts (in seconds)
    int DEFAULT_TIMEOUT = 10;
    int PAGE_LOAD_TIMEOUT = 30;
    int SCRIPT_TIMEOUT = 30;
    int POLLING_INTERVAL = 500; // in milliseconds

    // File paths
    String SCREENSHOT_PATH = "screenshots/";
    String REPORT_PATH = "target/reports/";
    String TEST_DATA_PATH = "src/test/resources/testdata/";

    // Browser settings
    String BROWSER = "chrome";

    // Test execution settings
    int RETRY_COUNT = 2;

    // User credentials
    String DEFAULT_USERNAME = "Admin";
    String DEFAULT_PASSWORD = "admin123";
}