package com.example.core;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariOptions;

/**
 * Provides browser-specific options for WebDriver instances.
 * This class configures browser options for different browsers with consistent settings.
 */
public class BrowserOptions {

    /**
     * Gets Chrome browser options with specified headless mode.
     * @param headless Whether to run in headless mode
     * @return Configured ChromeOptions
     */
    public static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--incognito");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");

        if (headless) {
            options.addArguments("--headless=new"); // New headless mode
        }
        return options;
    }

    /**
     * Gets Firefox browser options with specified headless mode.
     * @param headless Whether to run in headless mode
     * @return Configured FirefoxOptions
     */
    public static FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--private");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");

        if (headless) {
            options.addArguments("--headless");
        }
        return options;
    }

    /**
     * Gets Edge browser options with specified headless mode.
     * @param headless Whether to run in headless mode
     * @return Configured EdgeOptions
     */
    public static EdgeOptions getEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--inprivate");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");

        if (headless) {
            options.addArguments("--headless");
        }
        return options;
    }

    /**
     * Gets Safari browser options.
     * Note: Safari does not support headless mode.
     * @return Configured SafariOptions
     */
    public static SafariOptions getSafariOptions() {
        SafariOptions options = new SafariOptions();
        // Safari has limited options compared to other browsers
        options.setAutomaticInspection(false);
        return options;
    }
}