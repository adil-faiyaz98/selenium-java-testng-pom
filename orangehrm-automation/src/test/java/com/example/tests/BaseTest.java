package com.example.tests;

import com.example.core.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {

    protected WebDriver driver;
    protected Logger logger;

    @BeforeClass
    public void setup() {
        logger = LogManager.getLogger(getClass());
        logger.info("Starting test class: " + getClass().getSimpleName());
        driver = WebDriverManager.getDriver();
    }

    @AfterClass
    public void teardown() {
        logger.info("Finishing test class: " + getClass().getSimpleName());
        WebDriverManager.quitDriver();
    }
}