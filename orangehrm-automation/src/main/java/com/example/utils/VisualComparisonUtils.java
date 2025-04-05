package com.example.utils;

import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for visual comparison testing.
 * This class provides methods to capture screenshots and compare them with baseline images.
 */
public class VisualComparisonUtils {

    private static final Logger logger = LogManager.getLogger(VisualComparisonUtils.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static final String BASELINE_DIR = Constants.REPORT_PATH + "visual/baseline/";
    private static final String ACTUAL_DIR = Constants.REPORT_PATH + "visual/actual/";
    private static final String DIFF_DIR = Constants.REPORT_PATH + "visual/diff/";
    private static final double DEFAULT_THRESHOLD = 0.05; // 5% difference threshold

    /**
     * Captures a screenshot of the current page.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return The path to the saved screenshot
     */
    public static String captureScreenshot(WebDriver driver, String pageName) {
        try {
            // Create directory if it doesn't exist
            Path screenshotDir = Paths.get(ACTUAL_DIR);
            Files.createDirectories(screenshotDir);
            
            // Capture screenshot
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            BufferedImage screenshot = ImageIO.read(new ByteArrayInputStream(screenshotBytes));
            
            // Save screenshot
            String timestamp = DATE_FORMAT.format(new Date());
            String filename = pageName + "_" + timestamp + ".png";
            Path screenshotPath = screenshotDir.resolve(filename);
            ImageIO.write(screenshot, "PNG", screenshotPath.toFile());
            
            logger.info("Screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Captures a screenshot of a specific element.
     * @param driver The WebDriver instance
     * @param element The WebElement to capture
     * @param elementName The name of the element
     * @return The path to the saved screenshot
     */
    public static String captureElementScreenshot(WebDriver driver, WebElement element, String elementName) {
        try {
            // Create directory if it doesn't exist
            Path screenshotDir = Paths.get(ACTUAL_DIR);
            Files.createDirectories(screenshotDir);
            
            // Capture full page screenshot
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            BufferedImage fullScreenshot = ImageIO.read(new ByteArrayInputStream(screenshotBytes));
            
            // Get element dimensions
            int elementX = element.getLocation().getX();
            int elementY = element.getLocation().getY();
            int elementWidth = element.getSize().getWidth();
            int elementHeight = element.getSize().getHeight();
            
            // Crop the screenshot to the element
            BufferedImage elementScreenshot = fullScreenshot.getSubimage(
                    elementX, elementY, elementWidth, elementHeight);
            
            // Save screenshot
            String timestamp = DATE_FORMAT.format(new Date());
            String filename = elementName + "_" + timestamp + ".png";
            Path screenshotPath = screenshotDir.resolve(filename);
            ImageIO.write(elementScreenshot, "PNG", screenshotPath.toFile());
            
            logger.info("Element screenshot captured: {}", screenshotPath);
            return screenshotPath.toString();
        } catch (IOException e) {
            logger.error("Failed to capture element screenshot: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Saves a screenshot as a baseline for future comparisons.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return The path to the saved baseline
     */
    public static String saveBaseline(WebDriver driver, String pageName) {
        try {
            // Create directory if it doesn't exist
            Path baselineDir = Paths.get(BASELINE_DIR);
            Files.createDirectories(baselineDir);
            
            // Capture screenshot
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            BufferedImage screenshot = ImageIO.read(new ByteArrayInputStream(screenshotBytes));
            
            // Save as baseline
            String filename = pageName + ".png";
            Path baselinePath = baselineDir.resolve(filename);
            ImageIO.write(screenshot, "PNG", baselinePath.toFile());
            
            logger.info("Baseline saved: {}", baselinePath);
            return baselinePath.toString();
        } catch (IOException e) {
            logger.error("Failed to save baseline: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Saves an element screenshot as a baseline for future comparisons.
     * @param driver The WebDriver instance
     * @param element The WebElement to capture
     * @param elementName The name of the element
     * @return The path to the saved baseline
     */
    public static String saveElementBaseline(WebDriver driver, WebElement element, String elementName) {
        try {
            // Create directory if it doesn't exist
            Path baselineDir = Paths.get(BASELINE_DIR);
            Files.createDirectories(baselineDir);
            
            // Capture full page screenshot
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            BufferedImage fullScreenshot = ImageIO.read(new ByteArrayInputStream(screenshotBytes));
            
            // Get element dimensions
            int elementX = element.getLocation().getX();
            int elementY = element.getLocation().getY();
            int elementWidth = element.getSize().getWidth();
            int elementHeight = element.getSize().getHeight();
            
            // Crop the screenshot to the element
            BufferedImage elementScreenshot = fullScreenshot.getSubimage(
                    elementX, elementY, elementWidth, elementHeight);
            
            // Save as baseline
            String filename = elementName + ".png";
            Path baselinePath = baselineDir.resolve(filename);
            ImageIO.write(elementScreenshot, "PNG", baselinePath.toFile());
            
            logger.info("Element baseline saved: {}", baselinePath);
            return baselinePath.toString();
        } catch (IOException e) {
            logger.error("Failed to save element baseline: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Compares a screenshot with a baseline image.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return A comparison result object
     */
    public static ComparisonResult compareWithBaseline(WebDriver driver, String pageName) {
        return compareWithBaseline(driver, pageName, DEFAULT_THRESHOLD);
    }

    /**
     * Compares a screenshot with a baseline image using a custom threshold.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @param threshold The difference threshold (0.0 to 1.0)
     * @return A comparison result object
     */
    public static ComparisonResult compareWithBaseline(WebDriver driver, String pageName, double threshold) {
        try {
            // Check if baseline exists
            Path baselinePath = Paths.get(BASELINE_DIR, pageName + ".png");
            if (!Files.exists(baselinePath)) {
                logger.warn("Baseline does not exist for page: {}", pageName);
                return new ComparisonResult(false, 1.0, null, null, null);
            }
            
            // Capture current screenshot
            String actualPath = captureScreenshot(driver, pageName);
            if (actualPath == null) {
                return new ComparisonResult(false, 1.0, null, null, null);
            }
            
            // Load images
            BufferedImage baseline = ImageIO.read(baselinePath.toFile());
            BufferedImage actual = ImageIO.read(new File(actualPath));
            
            // Resize actual image if dimensions don't match
            if (baseline.getWidth() != actual.getWidth() || baseline.getHeight() != actual.getHeight()) {
                logger.warn("Image dimensions don't match. Resizing actual image to match baseline.");
                actual = resizeImage(actual, baseline.getWidth(), baseline.getHeight());
            }
            
            // Compare images
            return compareImages(baseline, actual, pageName, threshold);
        } catch (IOException e) {
            logger.error("Failed to compare with baseline: {}", e.getMessage(), e);
            return new ComparisonResult(false, 1.0, null, null, null);
        }
    }

    /**
     * Compares an element with a baseline image.
     * @param driver The WebDriver instance
     * @param element The WebElement to capture
     * @param elementName The name of the element
     * @return A comparison result object
     */
    public static ComparisonResult compareElementWithBaseline(WebDriver driver, WebElement element, String elementName) {
        return compareElementWithBaseline(driver, element, elementName, DEFAULT_THRESHOLD);
    }

    /**
     * Compares an element with a baseline image using a custom threshold.
     * @param driver The WebDriver instance
     * @param element The WebElement to capture
     * @param elementName The name of the element
     * @param threshold The difference threshold (0.0 to 1.0)
     * @return A comparison result object
     */
    public static ComparisonResult compareElementWithBaseline(WebDriver driver, WebElement element, String elementName, double threshold) {
        try {
            // Check if baseline exists
            Path baselinePath = Paths.get(BASELINE_DIR, elementName + ".png");
            if (!Files.exists(baselinePath)) {
                logger.warn("Baseline does not exist for element: {}", elementName);
                return new ComparisonResult(false, 1.0, null, null, null);
            }
            
            // Capture current element screenshot
            String actualPath = captureElementScreenshot(driver, element, elementName);
            if (actualPath == null) {
                return new ComparisonResult(false, 1.0, null, null, null);
            }
            
            // Load images
            BufferedImage baseline = ImageIO.read(baselinePath.toFile());
            BufferedImage actual = ImageIO.read(new File(actualPath));
            
            // Resize actual image if dimensions don't match
            if (baseline.getWidth() != actual.getWidth() || baseline.getHeight() != actual.getHeight()) {
                logger.warn("Image dimensions don't match. Resizing actual image to match baseline.");
                actual = resizeImage(actual, baseline.getWidth(), baseline.getHeight());
            }
            
            // Compare images
            return compareImages(baseline, actual, elementName, threshold);
        } catch (IOException e) {
            logger.error("Failed to compare element with baseline: {}", e.getMessage(), e);
            return new ComparisonResult(false, 1.0, null, null, null);
        }
    }

    /**
     * Compares two images and generates a difference image.
     * @param baseline The baseline image
     * @param actual The actual image
     * @param name The name of the image
     * @param threshold The difference threshold
     * @return A comparison result object
     */
    private static ComparisonResult compareImages(BufferedImage baseline, BufferedImage actual, String name, double threshold) {
        try {
            // Create directories if they don't exist
            Path diffDir = Paths.get(DIFF_DIR);
            Files.createDirectories(diffDir);
            
            // Create difference image
            BufferedImage diff = new BufferedImage(baseline.getWidth(), baseline.getHeight(), BufferedImage.TYPE_INT_RGB);
            
            // Compare pixels and calculate difference percentage
            long differentPixels = 0;
            long totalPixels = baseline.getWidth() * baseline.getHeight();
            
            for (int y = 0; y < baseline.getHeight(); y++) {
                for (int x = 0; x < baseline.getWidth(); x++) {
                    int baselineRGB = baseline.getRGB(x, y);
                    int actualRGB = actual.getRGB(x, y);
                    
                    // Extract RGB components
                    int baselineRed = (baselineRGB >> 16) & 0xFF;
                    int baselineGreen = (baselineRGB >> 8) & 0xFF;
                    int baselineBlue = baselineRGB & 0xFF;
                    
                    int actualRed = (actualRGB >> 16) & 0xFF;
                    int actualGreen = (actualRGB >> 8) & 0xFF;
                    int actualBlue = actualRGB & 0xFF;
                    
                    // Calculate color difference
                    int diffRed = Math.abs(baselineRed - actualRed);
                    int diffGreen = Math.abs(baselineGreen - actualGreen);
                    int diffBlue = Math.abs(baselineBlue - actualBlue);
                    
                    // If pixels are different, mark them in the diff image
                    if (diffRed > 0 || diffGreen > 0 || diffBlue > 0) {
                        differentPixels++;
                        // Highlight the difference in red
                        diff.setRGB(x, y, new Color(255, 0, 0).getRGB());
                    } else {
                        // Keep the original pixel
                        diff.setRGB(x, y, baselineRGB);
                    }
                }
            }
            
            // Calculate difference percentage
            double diffPercentage = (double) differentPixels / totalPixels;
            
            // Save diff image
            String timestamp = DATE_FORMAT.format(new Date());
            String filename = name + "_diff_" + timestamp + ".png";
            Path diffPath = diffDir.resolve(filename);
            ImageIO.write(diff, "PNG", diffPath.toFile());
            
            // Determine if the comparison passed
            boolean passed = diffPercentage <= threshold;
            
            logger.info("Image comparison for {}: Difference: {:.2f}%, Threshold: {:.2f}%, Result: {}", 
                    name, diffPercentage * 100, threshold * 100, passed ? "PASSED" : "FAILED");
            
            return new ComparisonResult(
                    passed,
                    diffPercentage,
                    baselinePath(name),
                    actualPath(name, timestamp),
                    diffPath.toString()
            );
        } catch (IOException e) {
            logger.error("Failed to compare images: {}", e.getMessage(), e);
            return new ComparisonResult(false, 1.0, null, null, null);
        }
    }

    /**
     * Resizes an image to the specified dimensions.
     * @param image The image to resize
     * @param width The target width
     * @param height The target height
     * @return The resized image
     */
    private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * Gets the path to a baseline image.
     * @param name The name of the image
     * @return The path to the baseline image
     */
    private static String baselinePath(String name) {
        return Paths.get(BASELINE_DIR, name + ".png").toString();
    }

    /**
     * Gets the path to an actual image.
     * @param name The name of the image
     * @param timestamp The timestamp
     * @return The path to the actual image
     */
    private static String actualPath(String name, String timestamp) {
        return Paths.get(ACTUAL_DIR, name + "_" + timestamp + ".png").toString();
    }

    /**
     * Class representing the result of an image comparison.
     */
    public static class ComparisonResult {
        private final boolean passed;
        private final double diffPercentage;
        private final String baselinePath;
        private final String actualPath;
        private final String diffPath;
        private final Map<String, Object> metadata;

        /**
         * Constructor for ComparisonResult.
         * @param passed Whether the comparison passed
         * @param diffPercentage The difference percentage
         * @param baselinePath The path to the baseline image
         * @param actualPath The path to the actual image
         * @param diffPath The path to the difference image
         */
        public ComparisonResult(boolean passed, double diffPercentage, String baselinePath, String actualPath, String diffPath) {
            this.passed = passed;
            this.diffPercentage = diffPercentage;
            this.baselinePath = baselinePath;
            this.actualPath = actualPath;
            this.diffPath = diffPath;
            this.metadata = new HashMap<>();
        }

        /**
         * Gets whether the comparison passed.
         * @return true if the comparison passed, false otherwise
         */
        public boolean isPassed() {
            return passed;
        }

        /**
         * Gets the difference percentage.
         * @return The difference percentage (0.0 to 1.0)
         */
        public double getDiffPercentage() {
            return diffPercentage;
        }

        /**
         * Gets the path to the baseline image.
         * @return The path to the baseline image
         */
        public String getBaselinePath() {
            return baselinePath;
        }

        /**
         * Gets the path to the actual image.
         * @return The path to the actual image
         */
        public String getActualPath() {
            return actualPath;
        }

        /**
         * Gets the path to the difference image.
         * @return The path to the difference image
         */
        public String getDiffPath() {
            return diffPath;
        }

        /**
         * Adds metadata to the comparison result.
         * @param key The metadata key
         * @param value The metadata value
         * @return This ComparisonResult instance for method chaining
         */
        public ComparisonResult addMetadata(String key, Object value) {
            metadata.put(key, value);
            return this;
        }

        /**
         * Gets the metadata.
         * @return The metadata map
         */
        public Map<String, Object> getMetadata() {
            return metadata;
        }

        @Override
        public String toString() {
            return String.format("ComparisonResult{passed=%s, diffPercentage=%.2f%%, baselinePath='%s', actualPath='%s', diffPath='%s'}",
                    passed, diffPercentage * 100, baselinePath, actualPath, diffPath);
        }
    }
}
