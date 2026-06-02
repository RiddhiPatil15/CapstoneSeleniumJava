package hooks;

import base.DriverFactory;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import utils.ScreenshotUtils;

public class Hooks {

    @Before(order = 0)
    public void setUp() {
        System.out.println("=== STARTING NEW SCENARIO - OPENING BROWSER ===");
        DriverFactory.initDriver();
    }

    @After(order = 0)
    public void tearDown() {
        try {
            System.out.println("=== SCENARIO END -  CLOSING BROWSER ===");
        } finally {
            DriverFactory.quitDriver();
        }
    }
    @After(order = 1)
    public void takeScreenshotOnFailure(Scenario scenario) {

        if (scenario.isFailed()) {

            String scenarioId = scenario.getId().replaceAll("[^a-zA-Z0-9]", "_");
            ScreenshotUtils.captureFailureScreenshot(DriverFactory.getDriver(), scenarioId);

            System.out.println("FAILURE SCREENSHOT CAPTURED");
        }
    }
}