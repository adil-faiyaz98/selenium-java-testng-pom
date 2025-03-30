package com.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotUtils {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);

    public static void takeScreenshot(WebDriver driver, String filename) {
        try {
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destPath = Paths.get("screenshots/" + filename);

            // Ensure the directory exists
            if (!destPath.getParent().toFile().exists()) {
                destPath.getParent().toFile().mkdirs();
            }

            Files.copy(sourceFile.toPath(), destPath);
            logger.info("Screenshot saved to: " + destPath.toString());

        } catch (IOException e) {
            logger.error("Failed to take screenshot: " + e.getMessage(), e);
        }
    }
}