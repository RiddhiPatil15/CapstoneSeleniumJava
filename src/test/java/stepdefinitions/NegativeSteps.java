package stepdefinitions;

import base.DriverFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import pages.NotesPage;
import pages.RegisterPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ExcelUtils;
import utils.ScreenshotUtils;
import utils.WaitUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class NegativeSteps {

    RegisterPage registerPage;
    LoginPage loginPage;
    String name;
    String email;
    String password;
    String confirmPassword;
    String expectedError;
    String category;
    String title;
    String description;
    NotesPage notesPage;
    String tcId;
    String apiScenarioType;
    int expectedStatus;
    String expectedMessage;
    Response apiResponse;

    // note: register negative case
    @When("negative user reads {string} from RegisterNegative sheet")
    public void negative_user_reads_from_register_negative_sheet(String tcId) {
        this.tcId = tcId;
        name = ExcelUtils.getCellData("RegisterNegative", tcId, "Name");
        email = ExcelUtils.getCellData("RegisterNegative", tcId, "Email");
        password = ExcelUtils.getCellData("RegisterNegative", tcId, "Password");
        confirmPassword = ExcelUtils.getCellData("RegisterNegative", tcId, "ConfirmPassword");
        expectedError = ExcelUtils.getCellData("RegisterNegative", tcId, "ExpectedError");
    }

    @When("user enters invalid registration details")
    public void user_enters_invalid_registration_details() {
        registerPage = new RegisterPage(DriverFactory.getDriver());
        registerPage.enterName(name);
        registerPage.enterEmail(email);
        registerPage.enterPassword(password);
        registerPage.enterConfirmPassword(confirmPassword);
    }

    @When("user submits invalid registration form")
    public void user_submits_invalid_registration_form() {
        registerPage.submitRegistration();
    }

    @Then("proper registration error should be shown")
    public void proper_registration_error_should_be_shown() {
        String screenshotName = "REGISTER_" + tcId;
        String actualError = registerPage.getValidationError();
        if (actualError == null || actualError.trim().isEmpty()) {
            actualError = registerPage.getBrowserValidationMessage(By.cssSelector("[data-testid='register-name']"));

            if (actualError == null || actualError.trim().isEmpty()) {
                actualError = registerPage.getBrowserValidationMessage(By.cssSelector("[data-testid='register-password']"));
            }
        }
        actualError = actualError.trim().toLowerCase();
        String expected = expectedError.trim().toLowerCase();
        ScreenshotUtils.captureScreenshot(DriverFactory.getDriver(), screenshotName);
        Assert.assertTrue(actualError.contains(expected));
    }

    // note: login negative case
    @When("negative user reads {string} from LoginNegative sheet")
    public void negative_user_reads_from_login_negative_sheet(String tcId) {
        this.tcId = tcId;
        email = ExcelUtils.getCellData("LoginNegative", tcId, "Email");
        password = ExcelUtils.getCellData("LoginNegative", tcId, "Password");
        expectedError = ExcelUtils.getCellData("LoginNegative", tcId, "ExpectedError");
    }

    @When("user enters invalid login details")
    public void user_enters_invalid_login_details() {
        loginPage = new LoginPage(DriverFactory.getDriver());
        DriverFactory.getDriver().get("https://practice.expandtesting.com/notes/app/login");
        loginPage.login(email, password);
    }

    @Then("proper login error should be shown")
    public void proper_login_error_should_be_shown() {

        String screenshotName = "LOGIN_" + tcId;
        String actualError = loginPage.getLoginValidationError();
        if (actualError == null) {
            actualError = "";
        }
        else {
            actualError = actualError.trim().toLowerCase();
        }
        String expected = expectedError.trim().toLowerCase();
        ScreenshotUtils.captureScreenshot(DriverFactory.getDriver(), screenshotName);
        Assert.assertTrue(actualError.contains(expected));
    }

    //note: negative note cases
    @Given("user opens the application and navigates to login page")
    public void open_app_and_go_to_login() {
        DriverFactory.getDriver().get("https://practice.expandtesting.com/notes/app/login");
        loginPage = new LoginPage(DriverFactory.getDriver());
    }

    @When("user logs in with valid credentials for notes")
    public void user_logs_in_with_valid_credentials_for_notes() {
        loginPage.login("A1001test@mail.com", "Pass@123");
    }

    @Then("dashboard should be visible for notes flow")
    public void dashboard_should_be_visible_for_notes_flow() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
        if (!loginPage.isLoginSuccessful()) {
            throw new AssertionError("Dashboard not visible for notes flow");
        }
        System.out.println("DASHBOARD VERIFIED (NEGATIVE NOTES FLOW)");
    }

    @When("user reads {string} from NotesNegative sheet")
    public void user_reads_from_notes_negative_sheet(String tcId) {
        this.tcId = tcId;
        category = ExcelUtils.getCellData("NotesNegative", tcId, "Category");
        title = ExcelUtils.getCellData("NotesNegative", tcId, "Title");
        description = ExcelUtils.getCellData("NotesNegative", tcId, "Description");
        expectedError = ExcelUtils.getCellData("NotesNegative", tcId, "Expected Error");
    }

        @When("user performs invalid notes actions")
        public void user_performs_invalid_notes_actions() {

            notesPage = new NotesPage(DriverFactory.getDriver());
            WebDriver driver = DriverFactory.getDriver();

            By addBtn = By.cssSelector("[data-testid='add-new-note']");
            By titleInput = By.cssSelector("[data-testid='note-title']");
            By descInput = By.cssSelector("[data-testid='note-description']");
            By submitBtn = By.cssSelector("[data-testid='note-submit']");

            WaitUtils.handleAds(driver);
            WaitUtils.safeHealClick(driver, addBtn);
            String t = title == null ? "" : title;
            String d = description == null ? "" : description;

            WaitUtils.HealType(driver, titleInput, t);
            WaitUtils.HealType(driver, descInput, d);

            WaitUtils.handleAds(driver);
            WaitUtils.scrollAndClick(driver, submitBtn);
            WaitUtils.handleAds(driver);
        }

    @Then("proper notes error should be shown")
    public void proper_notes_error_should_be_shown() {
        String screenshotName = "NOTES_" + tcId;
        String actualError = "";
        List<WebElement> errors = DriverFactory.getDriver().findElements(By.cssSelector(".invalid-feedback"));

        for (WebElement err : errors) {
            if (!err.getText().trim().isEmpty()) {
                actualError = err.getText().trim();
                break;
            }
        }
        if (actualError.isEmpty()) {
            throw new AssertionError("No validation error shown for notes");
        }
        actualError = actualError.toLowerCase().trim();
        expectedError = expectedError.toLowerCase().trim();

        ScreenshotUtils.captureScreenshot(DriverFactory.getDriver(), screenshotName);
        Assert.assertTrue(actualError.contains(expectedError));
    }

    //note: api negative case
    @When("API negative user reads {string} from ApiNegative sheet")
    public void api_negative_user_reads_from_api_negative_sheet(String tcId) {
        this.tcId = tcId;
        apiScenarioType = ExcelUtils.getCellData("ApiNegative", tcId, "ScenarioType");
        name = ExcelUtils.getCellData("ApiNegative", tcId, "Name");
        email = ExcelUtils.getCellData("ApiNegative", tcId, "Email");
        password = ExcelUtils.getCellData("ApiNegative", tcId, "Password");
        expectedMessage = ExcelUtils.getCellData("ApiNegative", tcId, "ExpectedMessage");
        expectedStatus = (int) Double.parseDouble(ExcelUtils.getCellData("ApiNegative", tcId, "ExpectedStatus"));
    }

    @When("user performs invalid API action")
    public void user_performs_invalid_api_action() {
        String BASE_URL = ConfigReader.getProperty("apiBaseUrl");
        Map<String, String> body = new HashMap<>();
        switch (apiScenarioType.toUpperCase()) {

            case "REGISTER":
                body.put("name", name);
                body.put("email", email);
                body.put("password", password);
                apiResponse =
                        RestAssured
                                .given()
                                .baseUri(BASE_URL)
                                .contentType("application/json")
                                .body(body)
                                .post("/users/register");
                break;

            case "LOGIN":
                body.put("email", email);
                body.put("password", password);
                apiResponse =
                        RestAssured
                                .given()
                                .baseUri(BASE_URL)
                                .contentType("application/json")
                                .body(body)
                                .post("/users/login");
                break;

            case "DELETE_NOTE":
                apiResponse =
                        RestAssured
                                .given()
                                .baseUri(BASE_URL)
                                .header(
                                        "x-auth-token",
                                        "INVALID_TOKEN"
                                )
                                .delete("/notes/INVALID_NOTE_ID");
                break;

            case "GET_NOTES":
                apiResponse =
                        RestAssured
                                .given()
                                .baseUri(BASE_URL)
                                .get("/notes");
                break;

            case "LOGOUT":
                apiResponse =
                        RestAssured
                                .given()
                                .baseUri(BASE_URL)
                                .delete("/users/logout");
                break;

            default:
                throw new RuntimeException("Invalid API negative scenario type");
        }
    }

    @Then("proper API error should be returned")
    public void proper_api_error_should_be_returned() {
        int actualStatus = apiResponse.getStatusCode();
        String responseBody = apiResponse.getBody().asString().toLowerCase();
        String expected = expectedMessage.toLowerCase();

        System.out.println("STATUS CODE: " + actualStatus);
        System.out.println("RESPONSE: " + responseBody);

        Assert.assertEquals(actualStatus, expectedStatus, "Incorrect status code");
        Assert.assertTrue(responseBody.contains(expected), "Expected message not found in response");
    }
}