package stepdefinitions;

import base.DriverFactory;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.RegisterPage;
import utils.ConfigReader;
import utils.ExcelUtils;
import java.io.File;
import java.io.FileWriter;

public class RegisterSteps {

    RegisterPage registerPage;
    public static String email;
    public static String password;
    String testCaseId;
    String name;
    String confirmPassword;

    @Given("user opens the application")
    public void user_opens_the_application() {

//        DriverFactory.getDriver().get(ConfigReader.getProperty("baseUrl"));
//        registerPage = new RegisterPage(DriverFactory.getDriver());

        WebDriver driver = DriverFactory.getDriver();
        driver.get(ConfigReader.getProperty("baseUrl"));
        registerPage = new RegisterPage(driver);
        registerPage.openRegisterForm();
    }

    @When("user reads {string} from Users sheet")
    public void user_reads_from_users_sheet(String tcId) {

        this.testCaseId = tcId;
        name = ExcelUtils.getUserData(tcId, "Name");
        email = ExcelUtils.getUserData(tcId, "Email");
        password = ExcelUtils.getUserData(tcId, "Password");
        confirmPassword = ExcelUtils.getUserData(tcId, "ConfirmPassword");
    }

    @When("user enters registration details")
    public void user_enters_registration_details() {

        registerPage.enterName(name);
        registerPage.enterEmail(email);
        registerPage.enterPassword(password);
        registerPage.enterConfirmPassword(confirmPassword);
    }

    @When("user submits the registration form")
    public void user_submits_the_registration_form() {

        try {
            registerPage.submitRegistration();
        } catch (Exception e) {
            logFailure("UserLogs", testCaseId, e.getMessage());
            throw e;
        }
    }

    @Then("user should see registration success message")
    public void user_should_see_registration_success_message() {

        // note: for existing user
        if (registerPage.isUserAlreadyExists()) {
            System.out.println("USER EXISTS ----> Navigating to login");
            registerPage.goToLoginFromExistingUserMessage();
            return;
        }

        //note: for new user
        String msg = registerPage.getRegistrationSuccessMessage();
        if (msg == null || msg.isEmpty()) {
            logFailure("UserLogs", testCaseId, "Empty success message");
            throw new AssertionError("Registration failed for " + testCaseId);
        }

        System.out.println("REGISTER SUCCESS: " + msg);
        registerPage.goToLoginFromSuccessFlow();
    }

    private void logFailure(String folder, String tcId, String reason) {

        try {
            new File(folder).mkdirs();

            FileWriter fw = new FileWriter(folder + "/" + tcId + "_failure.txt", true);
            fw.write("TEST CASE: " + tcId + "\n");
            fw.write("REASON: " + reason + "\n");
            fw.write("====================\n");
            fw.close();

        } catch (Exception ignored) {}
    }
}