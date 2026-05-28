package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
    By dashboardIndicator = By.xpath("//*[contains(text(),'Dashboard') or contains(text(),'Profile')]");
    By logoutBtn = By.xpath("//button[text()='Logout']");

    private void waitForLoginPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(email));
        wait.until(ExpectedConditions.visibilityOfElementLocated(password));
    }

    // note: login
    public void login(String userEmail, String userPassword) {
        waitForLoginPage();
        WebElement emailEle = driver.findElement(email);
        WebElement passEle = driver.findElement(password);
        emailEle.clear();
        emailEle.sendKeys(userEmail);
        passEle.clear();
        passEle.sendKeys(userPassword);
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    // note: dashboard
    public boolean isLoginSuccessful() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(dashboardIndicator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // note: logout
    public void logout() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(logoutBtn));
        }
    }

    // note: Negative Login Test Case
    public String getLoginValidationError() {
        try {
            //note: invalid creds.
            By toast = By.cssSelector("[data-testid='alert-message']");
            try {
                WebElement toastEle = wait.until(ExpectedConditions.visibilityOfElementLocated(toast));

                String text = toastEle.getText().trim();
                if (!text.isEmpty()) return text;
            }
            catch (Exception ignored) {
            }

            // note: missing fields
            By uiErrors = By.cssSelector(".invalid-feedback, .alert-danger, .text-danger");
            List<WebElement> uiEls = driver.findElements(uiErrors);

            for (WebElement ele : uiEls) {
                String text = ele.getText().trim();
                if (!text.isEmpty()) return text;
            }

            // note: HTML validation for email / pass
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