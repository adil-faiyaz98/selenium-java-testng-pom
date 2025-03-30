package com.example.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class TestUtils {

    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void takeScreenshot(WebDriver driver, String filename) {
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File screenshotFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
        Path destination = Paths.get("target", "screenshots");
        if (!Files.exists(destination)) {
            try {
                Files.createDirectories(destination);
            } catch (IOException e) {
                System.err.println("Error creating screenshot directory: " + e.getMessage());
                return;
            }
        }
        Path filePath = destination.resolve(filename + ".png");
        try {
            Files.copy(screenshotFile.toPath(), filePath);
            System.out.println("Screenshot saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving screenshot: " + e.getMessage());
        }
    }

    public static String getCurrentUrl(WebDriver driver) {
        return driver.getCurrentUrl();
    }

    public static String getPageTitle(WebDriver driver) {
        return driver.getTitle();
    }
}