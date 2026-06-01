package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class WaitUtils {

    private static final int DEFAULT_TIMEOUT = 30;

    private static WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    // note: visible
    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // note: clickable
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    // note: invisible
    public static boolean waitForInvisible(WebDriver driver, By locator) {
        return getWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // note: anything other than mentioned
    public static void waitForCondition(WebDriver driver, java.util.function.Function<WebDriver, Boolean> condition) {
        getWait(driver).until(condition);
    }

    // note: handle ad
    public static void handleAds(WebDriver driver) {

        try {
            driver.switchTo().defaultContent();
            //note: close button
            List<WebElement> closeBtns = driver.findElements(By.xpath("//div[text()='Close']"));
            if (!closeBtns.isEmpty()) {
                closeBtns.get(0).click();
                return;
            }
            //note: iframe ads
            List<WebElement> frames = driver.findElements(By.tagName("iframe"));
            for (WebElement frame : frames) {
                driver.switchTo().defaultContent();
                driver.switchTo().frame(frame);
                List<WebElement> close = driver.findElements(By.xpath("//div[text()='Close']"));

                if (!close.isEmpty()) {
                    close.get(0).click();
                    driver.switchTo().defaultContent();
                    return;
                }
            }
        }
        catch (Exception ignored) {
        }
        finally {
            driver.switchTo().defaultContent();
        }
    }


    public static void scrollAndClick(WebDriver driver, By locator) {
        WebElement ele = waitForVisible(driver, locator);
        new Actions(driver).scrollToElement(ele).perform();
        try {
            waitForClickable(driver, locator).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
        }
    }

    public static void safeHealClick(WebDriver driver, By locator) {
        try {
            handleAds(driver);
            waitForClickable(driver, locator).click();
        } catch (Exception e) {
            handleAds(driver);
            WebElement ele = waitForVisible(driver, locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
        }
    }

    public static void HealType(WebDriver driver, By locator, String value) {
        handleAds(driver);
        WebElement element = waitForVisible(driver, locator);
        new Actions(driver).scrollToElement(element).perform();
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }

        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
        element.sendKeys(value);
    }
}