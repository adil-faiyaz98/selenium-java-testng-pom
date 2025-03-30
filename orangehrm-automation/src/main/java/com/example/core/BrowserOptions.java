package com.example.core;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

public class BrowserOptions {

    public static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        // Add desired options here
        options.addArguments("--start-maximized");
        options.addArguments("--incognito");
        //options.addArguments("--headless=new"); // New headless mode
        return options;
    }

    public static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        // Add desired options here
        options.addArguments("--start-maximized");
        options.addArguments("--incognito");
        //options.addArguments("--headless");
        return options;
    }
}