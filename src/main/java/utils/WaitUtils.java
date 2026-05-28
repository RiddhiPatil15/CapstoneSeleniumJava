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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        WebElement ele = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ele);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
        }
    }

    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT)).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // note: Safe click forces js executor to bypass overlays of popups
    public static void safeHealClick(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            handleAds(driver);
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        }
        catch (Exception e) {
            handleAds(driver);
            WebElement ele = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
        }
    }

    public static void HealType(WebDriver driver, By locator, String value) {
        handleAds(driver);
        WebElement element = null;
        for (int i = 0; i < 4; i++) {
            try {
                element = waitForPresence(driver, locator);
                break;
            } catch (Exception e) {
                handleAds(driver);
            }
        }
        if (element == null) {
            throw new RuntimeException("Element not found even after retries: " + locator);
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        handleAds(driver);
        try {
            waitForClickable(driver, locator);
        } catch (Exception ignored) {
        }
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        try {
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.DELETE);
        } catch (Exception ignored) {}
        element.sendKeys(value);
    }

}