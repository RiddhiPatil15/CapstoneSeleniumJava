package runners.ui;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features/ui",
        glue = {"stepdefinitions", "hooks"},
        plugin = {
                "pretty",
                "html:reports/cucumber.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        }
)
public class UIRunner extends AbstractTestNGCucumberTests {
}