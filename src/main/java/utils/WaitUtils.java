package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitUtils {

    private static final int DEFAULT_TIMEOUT = 30;

    private static WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    // ---------------- VISIBILITY ----------------
    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return getWait(driver)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ---------------- CLICKABLE ----------------
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return getWait(driver)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    // ---------------- INVISIBILITY ----------------
    public static boolean waitForInvisible(WebDriver driver, By locator) {
        return getWait(driver)
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ---------------- CUSTOM CONDITION ----------------
    public static void waitForCondition(WebDriver driver, java.util.function.Function<WebDriver, Boolean> condition) {
        getWait(driver).until(condition);
    }

    // -------------------handles ads ------------------//
    public static void handleAds(WebDriver driver) {

        try {
            driver.switchTo().defaultContent();

            // try direct close button
            List<WebElement> closeBtns = driver.findElements(By.xpath("//div[text()='Close']"));
            if (!closeBtns.isEmpty()) {
                closeBtns.get(0).click();
                return;
            }

            // try iframe ads
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

        } catch (Exception ignored) {
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    public static void scrollAndClick(WebDriver driver, By locator) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        wait.until(ExpectedConditions.presenceOfElementLocated(locator));

        WebElement el = driver.findElement(locator);

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            // fallback JS click (bypasses ads/header overlay)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}