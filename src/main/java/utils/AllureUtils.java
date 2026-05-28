package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.ByteArrayInputStream;

public class AllureUtils {

    public static void attachText(String title, String content) {

        Allure.addAttachment(title, content);
    }
}