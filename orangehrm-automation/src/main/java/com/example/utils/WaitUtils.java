package com.example.utils;

import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Utility class for waiting operations in Selenium.
 * This class provides methods for waiting for various conditions in the browser.
 */
public class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    /**
     * Waits for an element to be visible.
     * @param driver The WebDriver instance
     * @param locator The locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @return The visible WebElement
     * @throws TimeoutException if the element is not visible within the timeout
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        logger.debug("Waiting for element to be visible: {}", locator);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for an element to be clickable.
     * @param driver The WebDriver instance
     * @param locator The locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @return The clickable WebElement
     * @throws TimeoutException if the element is not clickable within the timeout
     */
    public static WebElement waitForElementClickable(WebDriver driver, By locator, int timeoutInSeconds) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits for a URL to contain a specific string.
     * @param driver The WebDriver instance
     * @param urlPart The string that the URL should contain
     * @param timeoutInSeconds The timeout in seconds
     * @throws TimeoutException if the URL does not contain the string within the timeout
     */
    public static void waitForUrlContains(WebDriver driver, String urlPart, int timeoutInSeconds) {
        logger.debug("Waiting for URL to contain: {}", urlPart);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.urlContains(urlPart));
    }

    /**
     * Waits for an element to be present in the DOM.
     * @param driver The WebDriver instance
     * @param locator The locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @return The WebElement
     * @throws TimeoutException if the element is not present within the timeout
     */
    public static WebElement waitForElementPresent(WebDriver driver, By locator, int timeoutInSeconds) {
        logger.debug("Waiting for element to be present: {}", locator);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for an element to be invisible.
     * @param driver The WebDriver instance
     * @param locator The locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @throws TimeoutException if the element is still visible within the timeout
     */
    public static void waitForElementInvisible(WebDriver driver, By locator, int timeoutInSeconds) {
        logger.debug("Waiting for element to be invisible: {}", locator);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits for an element to have a specific text.
     * @param driver The WebDriver instance
     * @param locator The locator for the element
     * @param text The text to wait for
     * @param timeoutInSeconds The timeout in seconds
     * @return The WebElement with the text
     * @throws TimeoutException if the element does not have the text within the timeout
     */
    public static WebElement waitForElementWithText(WebDriver driver, By locator, String text, int timeoutInSeconds) {
        logger.debug("Waiting for element with text: {} in {}", text, locator);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text) ?
                driver.findElement(locator) : null);
    }

    /**
     * Waits for a list of elements to be present in the DOM.
     * @param driver The WebDriver instance
     * @param locator The locator for the elements
     * @param timeoutInSeconds The timeout in seconds
     * @return The list of WebElements
     * @throws TimeoutException if the elements are not present within the timeout
     */
    public static List<WebElement> waitForElementsPresent(WebDriver driver, By locator, int timeoutInSeconds) {
        logger.debug("Waiting for elements to be present: {}", locator);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Waits for a list of elements to be visible.
     * @param driver The WebDriver instance
     * @param locator The locator for the elements
     * @param timeoutInSeconds The timeout in seconds
     * @return The list of visible WebElements
     * @throws TimeoutException if the elements are not visible within the timeout
     */
    public static List<WebElement> waitForElementsVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        logger.debug("Waiting for elements to be visible: {}", locator);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * Waits for a page to load completely.
     * @param driver The WebDriver instance
     * @param timeoutInSeconds The timeout in seconds
     * @throws TimeoutException if the page does not load within the timeout
     */
    public static void waitForPageLoad(WebDriver driver, int timeoutInSeconds) {
        logger.debug("Waiting for page to load");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Creates a fluent wait with custom settings.
     * @param driver The WebDriver instance
     * @param timeoutInSeconds The timeout in seconds
     * @param pollingIntervalInMillis The polling interval in milliseconds
     * @return The FluentWait instance
     */
    public static FluentWait<WebDriver> createFluentWait(WebDriver driver, int timeoutInSeconds, int pollingIntervalInMillis) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofMillis(pollingIntervalInMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Waits for a custom condition with a fluent wait.
     * @param driver The WebDriver instance
     * @param condition The condition to wait for
     * @param timeoutInSeconds The timeout in seconds
     * @param <T> The type of the expected result
     * @return The result of the condition
     * @throws TimeoutException if the condition is not met within the timeout
     */
    public static <T> T waitForCondition(WebDriver driver, ExpectedCondition<T> condition, int timeoutInSeconds) {
        logger.debug("Waiting for custom condition");
        FluentWait<WebDriver> wait = createFluentWait(driver, timeoutInSeconds, Constants.POLLING_INTERVAL);
        return wait.until(condition);
    }
}