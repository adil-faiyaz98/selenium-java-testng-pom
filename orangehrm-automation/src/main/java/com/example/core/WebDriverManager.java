package com.example.core;

import com.example.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverManager {

    private static WebDriver driver;


    private WebDriverManager() {
        // Private constructor to prevent instantiation
    }

     public static WebDriver getDriver() {
        if (driver == null) {
           String browserType = ConfigReader.getProperty("browser");
            driver = createDriver(browserType);
        }
         return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

     private static WebDriver createDriver(String browserType) {

        switch (browserType.toLowerCase()) {
             case "chrome":
                 WebDriverManager.chromedriver().setup();
                 return new ChromeDriver(BrowserOptions.getChromeOptions());
             case "firefox":
                 WebDriverManager.firefoxdriver().setup();
                 return new FirefoxDriver(BrowserOptions.getFirefoxOptions());
             default:
                 throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
    }
}