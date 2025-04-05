package com.example.core;

import com.example.config.ConfigReader;
import com.example.config.Constants;
import io.github.bonigarcia.wdm.WebDriverManager as BrowserDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Manages WebDriver instances for browser automation.
 * This class is responsible for creating, configuring, and disposing of WebDriver instances.
 */
public class DriverFactory {

    private static WebDriver driver;
    private static WebDriverWait wait;

    /**
     * Private constructor to prevent instantiation
     */
    private DriverFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the WebDriver instance, creating it if necessary.
     * @return The WebDriver instance
     */
    public static WebDriver getDriver() {
        if (driver == null) {
            String browserType = ConfigReader.getProperty("browser");
            driver = createDriver(browserType);
            configureDriver();
        }
        return driver;
    }

    /**
     * Gets a WebDriverWait instance configured with the default timeout.
     * @return The WebDriverWait instance
     */
    public static WebDriverWait getWait() {
        if (wait == null) {
            wait = new WebDriverWait(getDriver(), Duration.ofSeconds(Constants.DEFAULT_TIMEOUT));
        }
        return wait;
    }

    /**
     * Quits the WebDriver instance and releases resources.
     */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            wait = null;
        }
    }

    /**
     * Creates a WebDriver instance based on the specified browser type.
     * @param browserType The type of browser to create
     * @return The WebDriver instance
     */
    private static WebDriver createDriver(String browserType) {
        boolean isHeadless = Boolean.parseBoolean(ConfigReader.getProperty("headless", "false"));

        switch (browserType.toLowerCase()) {
            case "chrome":
                BrowserDriverManager.chromedriver().setup();
                return new ChromeDriver(BrowserOptions.getChromeOptions(isHeadless));
            case "firefox":
                BrowserDriverManager.firefoxdriver().setup();
                return new FirefoxDriver(BrowserOptions.getFirefoxOptions(isHeadless));
            case "edge":
                BrowserDriverManager.edgedriver().setup();
                return new EdgeDriver(BrowserOptions.getEdgeOptions(isHeadless));
            case "safari":
                BrowserDriverManager.safaridriver().setup();
                return new SafariDriver(BrowserOptions.getSafariOptions());
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }

    /**
     * Configures the WebDriver instance with common settings.
     */
    private static void configureDriver() {
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Constants.DEFAULT_TIMEOUT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Constants.PAGE_LOAD_TIMEOUT));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(Constants.SCRIPT_TIMEOUT));
        driver.get(Constants.BASE_URL);
    }
}