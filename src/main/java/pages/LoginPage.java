package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.WaitUtils;

import java.time.Duration;
import java.util.List;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    By email = By.cssSelector("[data-testid='login-email']");
    By password = By.cssSelector("[data-testid='login-password']");
    By loginBtn = By.xpath("//button[text()='Login']");
    By dashboardIndicator = By.xpath("//*[@data-testid='profile']");
    By logoutBtn = By.xpath("//button[text()='Logout']");

    private void waitForLoginPage() {
       WaitUtils.waitForVisible(driver, email);
        WaitUtils.waitForVisible(driver, password);
    }

    // note: login
    public void login(String userEmail, String userPassword) {
        waitForLoginPage();
        WaitUtils.HealType(driver, email, userEmail);
        WaitUtils.HealType(driver, password, userPassword);
        WebElement btn = WaitUtils.waitForClickable(driver, loginBtn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    // note: dashboard
    public boolean isLoginSuccessful() {
        try {
            WaitUtils.waitForVisible(driver, dashboardIndicator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // note: logout
    public void logout() {
        WaitUtils.safeHealClick(driver, logoutBtn);
    }

    // note: Negative Login Test Case
    public String getLoginValidationError() {
        try {
            //note: invalid creds. --- trying to register
            By toast = By.cssSelector("[data-testid='alert-message']");
            try {
                WebElement toastEle = wait.until(ExpectedConditions.visibilityOfElementLocated(toast));
                String text = toastEle.getText().trim();
                if (!text.isEmpty()) return text;
            }
            catch (Exception ignored) {
            }

            // note: missing fields ------ invalid email format
            By uiErrors = By.cssSelector(".invalid-feedback, .alert-danger, .text-danger");
            List<WebElement> uiEls = driver.findElements(uiErrors);
            for (WebElement ele : uiEls) {
                String text = ele.getText().trim();
                if (!text.isEmpty()) return text;
            }

            // note: HTML validation for email / pass ---- missing email/password
            WebElement email = driver.findElement(By.cssSelector("[data-testid='login-email']"));
            String msg = email.getAttribute("validationMessage");
            if (msg != null && !msg.trim().isEmpty()) return msg;

            WebElement password = driver.findElement(By.cssSelector("[data-testid='login-password']"));
            msg = password.getAttribute("validationMessage");
            if (msg != null && !msg.trim().isEmpty()) return msg;

            return "";

        } catch (Exception e) {
            return "";
        }
    }
}