package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import io.qameta.allure.Allure;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ScreenshotUtils {

    public static String captureScreenshot(WebDriver driver, String fileName) {
        String path = System.getProperty("user.dir") + "/reports/screenshots/" + fileName + ".png";
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(path);
            Files.copy(source.toPath(), destination.toPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static void captureFailureScreenshot(WebDriver driver, String scenarioName) {
        String safeName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_");
        String path = System.getProperty("user.dir") + "/reports/screenshots/FailedCases/" + "FAILED_" + safeName + ".png";
        try {
            if (driver == null) {
                System.out.println("Driver is null. Screenshot skipped.");
                return;
            }
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(path);
            destination.getParentFile().mkdirs();
            Files.copy(source.toPath(), destination.toPath());

            try (FileInputStream fis = new FileInputStream(destination)) {
                Allure.addAttachment(
                        "Failure Screenshot - " + scenarioName,
                        fis
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}