package stepdefinitions;

import api.NotesApi;

import base.DriverFactory;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;

import static stepdefinitions.RegisterSteps.email;
import static stepdefinitions.RegisterSteps.password;


public class LoginSteps {

    public static NotesApi notesApi;

    LoginPage loginPage;
    public static String token;


    @Given("user is on login page")
    public void user_is_on_login_page() {
        WebDriver driver = DriverFactory.getDriver();
        loginPage = new LoginPage(driver);
    }

    @When("user logs in with stored credentials")
    public void user_logs_in_with_stored_credentials() {
        if (email == null || password == null) {
            throw new RuntimeException("No registered user found");
        }
        loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.login(email, password);
    }

    @Then("user dashboard should be visible")
    public void user_dashboard_should_be_visible() {
        if (!loginPage.isLoginSuccessful()) {
            throw new AssertionError("Dashboard not visible");
        }
        System.out.println("DASHBOARD VERIFIED");
        notesApi = new NotesApi();
        notesApi.authenticate(email, password);
    }

    @Then("user logs out")
    public void user_logs_out() {
        loginPage.logout();
    }

}