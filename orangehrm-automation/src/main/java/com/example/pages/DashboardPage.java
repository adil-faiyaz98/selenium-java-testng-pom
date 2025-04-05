package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DashboardPage {

    private WebDriver driver;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public AdminPage navigateToAdminPage() {
        WebElement adminLink = driver.findElement(By.xpath("//a[@href='/web/index.php/admin/viewAdminModule']"));
        adminLink.click();
        return new AdminPage(driver);
    }

    /**
     * Navigates to the PIM page.
     * @return The PIM page object
     */
    public PIMPage navigateToPIMPage() {
        WebElement pimLink = driver.findElement(By.xpath("//a[@href='/web/index.php/pim/viewPimModule']"));
        pimLink.click();
        return new PIMPage(driver);
    }

    public boolean isAdminMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Admin']"));
    }

    public boolean isPIMMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='PIM']"));
    }

    public boolean isLeaveMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Leave']"));
    }

    public boolean isTimeMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Time']"));
    }

    public boolean isRecruitmentMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Recruitment']"));
    }

    public boolean isMyInfoMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='My Info']"));
    }

    public boolean isPerformanceMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Performance']"));
    }

    public boolean isDashboardMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Dashboard']"));
    }

    public boolean isDirectoryMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Directory']"));
    }

    public boolean isMaintenanceMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Maintenance']"));
    }

    public boolean isClaimMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Claim']"));
    }

    public boolean isBuzzMenuPresent() {
        return isElementPresent(By.xpath("//span[text()='Buzz']"));
    }

    public boolean isMenuBarDashboardPresent() {
        return isElementPresent(By.xpath("//a[contains(@class,'oxd-topbar-body-nav-tab') and contains(text(),'Dashboard')]"));
    }

    public boolean isUpgradeButtonPresent() {
        return isElementPresent(By.xpath("//a[contains(@class,'oxd-topbar-body-nav-tab') and contains(text(),'Upgrade')]"));
    }

    public boolean isProfileDropdownPresent() {
        return isElementPresent(By.className("oxd-userdropdown-tab"));
    }

    public boolean isAboutOptionPresent() {
        if (isProfileDropdownPresent()) {
             driver.findElement(By.className("oxd-userdropdown-tab")).click();
             return isElementPresent(By.xpath("//a[text()='About']"));
        }
        return false;
    }

    public boolean isSupportOptionPresent() {
       if (isProfileDropdownPresent()) {
            driver.findElement(By.className("oxd-userdropdown-tab")).click();
            return isElementPresent(By.xpath("//a[text()='Support']"));
       }
       return false;
    }

    public boolean isChangePasswordOptionPresent() {
        if (isProfileDropdownPresent()) {
            driver.findElement(By.className("oxd-userdropdown-tab")).click();
            return isElementPresent(By.xpath("//a[text()='Change Password']"));
        }
        return false;
    }

    public boolean isLogoutOptionPresent() {
        if (isProfileDropdownPresent()) {
            driver.findElement(By.className("oxd-userdropdown-tab")).click();
            return isElementPresent(By.xpath("//a[text()='Logout']"));
        }
        return false;
    }

    public boolean isTimeAtWorkWidgetPresent() {
        return isElementPresent(By.xpath("//p[text()='Time at Work']"));
    }

    public boolean isMyActionsWidgetPresent() {
        return isElementPresent(By.xpath("//p[text()='My Actions']"));
    }

    public boolean isQuickLaunchWidgetPresent() {
        return isElementPresent(By.xpath("//p[text()='Quick Launch']"));
    }

     private boolean isElementPresent(By by) {
        return driver.findElements(by).size() > 0;
    }
}