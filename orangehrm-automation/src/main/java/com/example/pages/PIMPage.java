package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PIMPage {
    private WebDriver driver;

    public PIMPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isPageTitlePresent() {
        return driver.getTitle().equals("OrangeHRM");
    }

    public boolean isPIMMenuHighlighted() {
        WebElement pimMenu = driver.findElement(By.xpath("//span[text()='PIM']/parent::a")); // Assuming the active menu item has a specific style
        return pimMenu.getAttribute("class").contains("active"); // Replace "active" with the actual class or attribute indicating the active state
    }

    public boolean isConfigurationMenuPresent() {
        return driver.findElement(By.xpath("//li[contains(@class, 'oxd-topbar-body-nav-tab') and .//span[text()='Configuration']]")).isDisplayed();
    }

    public boolean isOptionalFieldsPresent() {
        return driver.findElement(By.xpath("//a[text()='Optional Fields']")).isDisplayed();
    }

    public boolean isCustomFieldsPresent() {
        return driver.findElement(By.xpath("//a[text()='Custom Fields']")).isDisplayed();
    }

    public boolean isDataImportPresent() {
        return driver.findElement(By.xpath("//a[text()='Data Import']")).isDisplayed();
    }

    public boolean isReportingMethodsPresent() {
        return driver.findElement(By.xpath("//a[text()='Reporting Methods']")).isDisplayed();
    }

    public boolean isTerminationReasonsPresent() {
        return driver.findElement(By.xpath("//a[text()='Termination Reasons']")).isDisplayed();
    }

    public boolean isEmployeeListMenuPresent() {
        return driver.findElement(By.xpath("//a[text()='Employee List']")).isDisplayed();
    }

    public boolean isAddEmployeeMenuPresent() {
        return driver.findElement(By.xpath("//a[text()='Add Employee']")).isDisplayed();
    }

    public boolean isReportsMenuPresent() {
        return driver.findElement(By.xpath("//a[text()='Reports']")).isDisplayed();
    }


    public boolean isUserJohnFighterPresent() {
        return driver.findElement(By.xpath("//p[@class='oxd-userdropdown-name']")).getText().contains("John fighter");
    }

    public boolean isUserProfileImagePresent() {
        return driver.findElement(By.xpath("//img[@alt='profile picture']")).isDisplayed();
    }

    public boolean isFooterCopyrightPresent() {
        WebElement footer = driver.findElement(By.className("orangehrm-copyright"));
        return footer.getText().contains("OrangeHRM OS 5.7") && footer.getText().contains("© 2005 - 2025 OrangeHRM, Inc. All rights reserved.");
    }

    public boolean isBreadcrumbPIMPresent() {
        return driver.findElement(By.xpath("//li[@class='oxd-breadcrumb-item']/a[text()='PIM']")).isDisplayed();
    }


}