package com.example.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class JavaScriptExecutorUtil {

    public static Object executeScript(WebDriver driver, String script, Object... args) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        return jsExecutor.executeScript(script, args);
    }

    public static void highlightElement(WebDriver driver, WebElement element) {
        executeScript(driver, "arguments[0].style.border='3px solid red'", element);
    }
}