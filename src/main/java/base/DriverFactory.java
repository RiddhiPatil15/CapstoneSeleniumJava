package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.ConfigReader;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    // note: while parallel execution, it helps each scenario gets its own browser

    public static void initDriver() {
        String browser = ConfigReader.getProperty("browser");
        WebDriver webDriver;

        if (browser.equalsIgnoreCase("chrome")) {
            webDriver = new ChromeDriver();

        }
        else {
            webDriver = new ChromeDriver(); // note: could be used for edge, just a fallback!
        }

        webDriver.manage().window().maximize();
        driver.set(webDriver);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {

        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}