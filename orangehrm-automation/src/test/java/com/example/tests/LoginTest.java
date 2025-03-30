import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeMethod
    public void setup() {
        // Initialize ChromeDriver
        driver = new ChromeDriver();
        // Initialize LoginPage
        loginPage = new LoginPage(driver);
    }

    @Test
    public void testLogin() {
        // Navigate to the login page
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

        // Enter username
        loginPage.enterUsername("Admin");

        // Enter password
        loginPage.enterPassword("admin123");

        // Click the login button
        loginPage.clickLoginButton();

        // Assert that the current URL contains '/dashboard'
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"), "Login failed: URL does not contain '/dashboard'");
    }

    @AfterMethod
    public void teardown() {
        // Close the WebDriver
        if (driver != null) {
            driver.quit();
        }
    }

    // LoginPage class (You can put this in a separate file if needed)
    private static class LoginPage {
        private WebDriver driver;

        public LoginPage(WebDriver driver) {
            this.driver = driver;
        }

        public void enterUsername(String username) {
            WebElement usernameField = driver.findElement(By.name("username"));
            usernameField.sendKeys(username);
        }

        public void enterPassword(String password) {
            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.sendKeys(password);
        }

        public void clickLoginButton() {
            WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
            loginButton.click();
        }
    }
}