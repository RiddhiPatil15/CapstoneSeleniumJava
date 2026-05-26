package runners.negative;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features/negative",
        glue = {"stepdefinitions", "hooks"},
        tags = "@login",
        plugin = {
                "pretty",
                "html:reports/cucumber.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true,
        dryRun = false
)
public class NegativeRunner extends AbstractTestNGCucumberTests {
}