package pages;

import org.openqa.selenium.*;
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
    //By adCloseBtn = By.xpath("//div[text()='Close']");

    By existingUserToast = By.cssSelector("[data-testid='alert-message']");
    By loginHereLink = By.xpath("//span[contains(text(),'Log in here')]");
//
//    private By validationError = By.cssSelector(".invalid-feedback");
//
//    private By alertError = By.cssSelector(".alert-danger");


    // ---------------- ADS ----------------
//    private void handleAds() {
//        try {
//            driver.switchTo().defaultContent();
//
//            List<WebElement> closeBtns = driver.findElements(adCloseBtn);
//            if (!closeBtns.isEmpty()) {
//                closeBtns.get(0).click();
//                return;
//            }
//
//            List<WebElement> frames = driver.findElements(By.tagName("iframe"));
//
//            for (WebElement frame : frames) {
//                driver.switchTo().defaultContent();
//                driver.switchTo().frame(frame);
//
//                List<WebElement> close = driver.findElements(adCloseBtn);
//                if (!close.isEmpty()) {
//                    close.get(0).click();
//                    driver.switchTo().defaultContent();
//                    return;
//                }
//            }
//
//        } catch (Exception ignored) {
//        } finally {
//            driver.switchTo().defaultContent();
//        }
//    }

    // note: Safe click forces js executor to bypass overlays of popups
    private void safeClick(By locator) {
        try {
            //handleAds();
            WaitUtils.handleAds(driver);
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            //handleAds();
            WaitUtils.handleAds(driver);
            WebElement ele = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
        }
    }

    private void type(By locator, String value) {

        WaitUtils.handleAds(driver);
        WebElement element = null;

        // note: retrying to find element, handles dynamic DOM chnages
        for (int i = 0; i < 3; i++) {
            try {
                element = WaitUtils.waitForPresence(driver, locator);
                break;
            } catch (Exception e) {
                WaitUtils.handleAds(driver);
            }
        }
        if (element == null) {
            throw new RuntimeException("Element not found even after retries: " + locator);
        }

        // note: brings element to center of screen, so it is not intercepted by overlays
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);

        WaitUtils.handleAds(driver);

        try {
            WaitUtils.waitForClickable(driver, locator);
        } catch (Exception ignored) {
            // note:  will continue anyways, as ad may still block, it will still try to proceed
            // it doesn't mean element isn't present, but because of DOM might still be loading, selenium is unsure
            // element may still work
        }

        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }

        // note: just a safe clear, before entering text
        try {
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.DELETE);
        } catch (Exception ignored) {}

        element.sendKeys(value);
    }

    // ---------------- ACTIONS ----------------
    public void openRegisterForm() {
        //handleAds();
        WaitUtils.handleAds(driver);
        safeClick(createAccountBtn);
    }

    public void enterEmail(String value) { type(email, value); }
    public void enterName(String value) { type(name, value); }
    public void enterPassword(String value) { type(password, value); }
    public void enterConfirmPassword(String value) { type(confirmPassword, value); }

    public void submitRegistration() {
        //handleAds();
        WaitUtils.handleAds(driver);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(registerBtn)
        );
        try {
            button.click();
        } catch (Exception e) {

            // fallback only if normal click fails, in case of ad/banner overlay
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    public String getRegistrationSuccessMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successMsg)).getText();
    }

    public boolean isUserAlreadyExists() {
        try {
            String msg = wait.until(ExpectedConditions.visibilityOfElementLocated(existingUserToast)).getText();
            return msg.toLowerCase().contains("already exists");
        } catch (Exception e) {
            return false;
        }
    }

    public void goToLoginFromExistingUserMessage() {

        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(loginHereLink)
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        wait.until(ExpectedConditions.elementToBeClickable(loginHereLink));

        try {
            link.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='login-email']")));
    }

    public void goToLoginFromSuccessFlow() {

        //handleAds();
        WaitUtils.handleAds(driver);
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        link.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='login-email']")));
    }

    public String getValidationError() {

        try {
            List<WebElement> errors = driver.findElements(By.cssSelector(".invalid-feedback, .alert-danger, .error, .text-danger"));

            for (WebElement el : errors) {
                String text = el.getText().trim();
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

//    public String getNameValidationMessage() {
//        return getBrowserValidationMessage(name);
//    }
//
//    public String getPasswordValidationMessage() {
//        return getBrowserValidationMessage(password);
//    }
//
//    public String getEmailValidationMessage() {
//
//        return getBrowserValidationMessage(email);
//    }
//
//    public String getActiveElementValidationMessage() {
//
//        WebElement active = driver.switchTo().activeElement();
//
//        return (String) ((JavascriptExecutor) driver)
//                .executeScript(
//                        "return arguments[0].validationMessage;",
//                        active
//                );
//    }

}