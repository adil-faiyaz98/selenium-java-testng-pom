package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminPage {
    private WebDriver driver;

    public AdminPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isPageTitleCorrect() {
        return driver.getTitle().equals("OrangeHRM");
    }

    public boolean isAdminMenuHighlighted() {
        WebElement adminMenuItem = driver.findElement(By.xpath("//a[@href='/web/index.php/admin/viewAdminModule']")); // Assuming this is the locator for the Admin menu item
        return adminMenuItem.findElement(By.xpath("..")).getAttribute("class").contains("active"); // Check if parent element has "active" class
    }

    public boolean isUserManagementMenuPresent() {
        return isMenuItemPresent("//span[text()='User Management']");
    }

    public boolean isUsersSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewSystemUsers')]");
    }

    public boolean isJobMenuPresent() {
        return isMenuItemPresent("//span[text()='Job']");
    }

    public boolean isJobTitlesSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewJobTitleList')]");
    }

    public boolean isPayGradesSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewPayGrades')]");
    }

    public boolean isEmploymentStatusSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/employmentStatus')]");
    }

    public boolean isJobCategoriesSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/jobCategory')]");
    }

    public boolean isWorkShiftsSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/workShift')]");
    }

    public boolean isOrganizationMenuPresent() {
        return isMenuItemPresent("//span[text()='Organization']");
    }

    public boolean isGeneralInformationSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewOrganizationGeneralInformation')]");
    }

    public boolean isLocationsSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewLocations')]");
    }

    public boolean isStructureSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewCompanyStructure')]");
    }

    public boolean isQualificationsMenuPresent() {
        return isMenuItemPresent("//span[text()='Qualifications']");
    }

    public boolean isSkillsSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewSkills')]");
    }

    public boolean isEducationSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewEducation')]");
    }

    public boolean isLicensesSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewLicenses')]");
    }

    public boolean isLanguagesSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewLanguages')]");
    }

    public boolean isMembershipsSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/membership')]");
    }

    public boolean isNationalitiesMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/nationality')]");
    }

    public boolean isCorporateBrandingMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/addTheme')]");
    }

    public boolean isConfigurationMenuPresent() {
        return isMenuItemPresent("//span[text()='Configuration']");
    }

    public boolean isEmailConfigurationSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/listMailConfiguration')]");
    }

    public boolean isEmailSubscriptionsSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewEmailNotification')]");
    }

    public boolean isLocalizationSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/localization')]");
    }

    public boolean isLanguagePackagesSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/languagePackage')]");
    }

    public boolean isModulesSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/viewModules')]");
    }

    public boolean isSocialMediaAuthenticationSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/openIdProvider')]");
    }

    public boolean isRegisterOAuthClientSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/registerOAuthClient')]");
    }

    public boolean isLDAPConfigurationSubMenuPresent() {
        return isMenuItemPresent("//a[contains(@href, '/web/index.php/admin/ldapConfiguration')]");
    }

    private boolean isMenuItemPresent(String xpath) {
        try {
            return driver.findElement(By.xpath(xpath)).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }


    public boolean isUserInfoPresent() {
        try {
            WebElement userDropdown = driver.findElement(By.className("oxd-userdropdown-tab")); // Assuming class name for user dropdown
            return userDropdown.isDisplayed() && userDropdown.getText().contains("John fighter");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isFooterCopyrightPresent() {
        try {
            WebElement footer = driver.findElement(By.className("orangehrm-copyright")); // Assuming class name for footer
            String footerText = footer.getText();
            return footerText.contains("OrangeHRM OS 5.7") && footerText.contains("© 2005 - 2025 OrangeHRM, Inc. All rights reserved.");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isBreadcrumbCorrect() {
        try {
            WebElement breadcrumb = driver.findElement(By.className("oxd-breadcrumb")); // Assuming class name for breadcrumb
            String breadcrumbText = breadcrumb.getText();
            return breadcrumbText.contains("Admin") && breadcrumbText.contains("User Management");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
}