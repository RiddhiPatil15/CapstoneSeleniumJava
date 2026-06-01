package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.WaitUtils;
import java.time.Duration;
import java.util.List;

public class RegisterPage {

    WebDriver driver;
    WebDriverWait wait;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    By createAccountBtn = By.cssSelector("[data-testid='open-register-view']");
    By email = By.cssSelector("[data-testid='register-email']");
    By name = By.cssSelector("[data-testid='register-name']");
    By password = By.cssSelector("[data-testid='register-password']");
    By confirmPassword = By.cssSelector("[data-testid='register-confirm-password']");
    By registerBtn = By.xpath("//button[text()='Register']");
    By successMsg = By.xpath("//div[contains(@class,'alert-success')]//b");
    By loginLink = By.cssSelector("[data-testid='login-view']");
    By existingUserToast = By.cssSelector("[data-testid='alert-message']");
    By loginHereLink = By.xpath("//span[contains(text(),'Log in here')]");

    public void openRegisterForm() {
        WaitUtils.handleAds(driver);
        WaitUtils.safeHealClick(driver, createAccountBtn);
    }

    public void enterEmail(String value) { WaitUtils.HealType(driver, email, value); }
    public void enterName(String value) { WaitUtils.HealType(driver, name, value); }
    public void enterPassword(String value) { WaitUtils.HealType(driver, password, value); }
    public void enterConfirmPassword(String value) { WaitUtils.HealType(driver, confirmPassword, value); }

    public void submitRegistration() {
        WaitUtils.handleAds(driver);
         WebElement button = WaitUtils.waitForClickable(driver, registerBtn);
        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    public String getRegistrationSuccessMessage() {
        //return wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg)).getText();
        return WaitUtils.waitForVisible(driver, successMsg).getText();
    }

    public boolean isUserAlreadyExists() {
        try {
            String msg = WaitUtils.waitForVisible(driver, existingUserToast).getText();
            return msg.toLowerCase().contains("already exists");
        } catch (Exception e) {
            return false;
        }
    }

    public void goToLoginFromExistingUserMessage() {
        WebElement link = WaitUtils.waitForVisible(driver, loginHereLink);
        new Actions(driver).scrollToElement(link).perform();
        WaitUtils.waitForClickable(driver, loginHereLink);
        try {
            link.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }
         WaitUtils.waitForVisible(driver, By.cssSelector("[data-testid='login-email']"));
    }

    public void goToLoginFromSuccessFlow() {
        WaitUtils.handleAds(driver);
        WebElement link = WaitUtils.waitForClickable(driver, loginLink);
        new Actions(driver).scrollToElement(link).perform();
        try {
            link.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }
        WaitUtils.waitForVisible(driver, By.cssSelector("[data-testid='login-email']"));
    }

    public String getValidationError() {
        try {
            List<WebElement> errors = driver.findElements(By.cssSelector(".invalid-feedback, .alert-danger, .error, .text-danger"));
            for (WebElement ele : errors) {
                String text = ele.getText().trim();
                if (!text.isEmpty()) {
                    return text;
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getBrowserValidationMessage(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            if (elements.isEmpty()) {
                return "";
            }
            WebElement element = elements.get(0);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object msg = js.executeScript("return arguments[0].validationMessage;", element);
            return msg == null ? "" : msg.toString();
        } catch (Exception e) {
            return "";
        }
    }
}