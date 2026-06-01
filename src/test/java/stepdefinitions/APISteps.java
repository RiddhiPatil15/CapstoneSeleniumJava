package stepdefinitions;

import api.NotesApi;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.AllureUtils;
import utils.ConfigReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APISteps {
    private final String BASE_URL = ConfigReader.getProperty("apiBaseUrl");
    private final NotesApi notesApi = new NotesApi();
    //private String token;
    private String email;
    private String password;
    private String createdNoteId;
    private String createdNoteTitle;

    // note: log data for api
    private void log(String message) {
        try {
            new File("UserLogs").mkdirs();
            FileWriter writer = new FileWriter("UserLogs/API_Flow_Log.txt", true);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // note: register
    @Given("user registers via API with {string} {string} {string}")
    public void user_registers_via_api_with(String name, String email, String password) {

        this.email = email;
        this.password = password;
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);

        Response response = RestAssured
                .given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(body)
                .post("/users/register");

        AllureUtils.attachText("REGISTER REQUEST", body.toString());
        AllureUtils.attachText("REGISTER RESPONSE", response.asPrettyString());

        int statusCode = response.getStatusCode();
        log("REGISTER STATUS CODE: " + statusCode);
        if (statusCode == 201) {
            log("========================");
            log("REGISTER SUCCESS");
            log("EMAIL: " + email);
        }
        else if (statusCode == 409) {
            log("========================");
            log("USER ALREADY EXISTS");
            log("EMAIL: " + email);
        }
        else {
            throw new AssertionError("Registration failed: " + statusCode);
        }
    }

    // note: login
    @When("user logs in via API with {string} {string}")
    public void user_logs_in_via_api_with(String email, String password) {
        this.email = email;
        this.password = password;
        notesApi.authenticate(email, password);
        AllureUtils.attachText("LOGIN EMAIL", email);
        log("LOGIN SUCCESS");
    }

    // note: create note
    @When("user creates note via API with {string} {string} {string}")
    public void user_creates_note_via_api_with(String category, String title, String description) {
        createdNoteTitle = title;
        Response response = notesApi.createNote(title, description, category);

        AllureUtils.attachText("CREATE NOTE TITLE", title);
        AllureUtils.attachText("CREATE NOTE RESPONSE", response.asPrettyString());

        if (response.getStatusCode() != 200 && response.getStatusCode() != 201) {
            throw new AssertionError("Create note failed");
        }
        createdNoteId = response.jsonPath().getString("data.id");
        log("NOTE CREATED");
        log("TITLE: " + title);
        log("CATEGORY: " + category);
    }

    // note: verify note
    @Then("note should exist in API response")
    public void note_should_exist_in_api_response() {
        Response response = notesApi.getNotes();
        AllureUtils.attachText("GET NOTES RESPONSE", response.asPrettyString());
        List<String> titles = response.jsonPath().getList("data.title");
        if (!titles.contains(createdNoteTitle)) {
            throw new AssertionError("Created note not found");
        }
        log("NOTE VERIFIED IN GET RESPONSE");
    }

    // note: delete note
    @When("user deletes created note via API")
    public void user_deletes_created_note_via_api() {
        Response response = notesApi.deleteNote(createdNoteId);
        AllureUtils.attachText("DELETE NOTE RESPONSE", response.asPrettyString());
        if (response.getStatusCode() != 200) {
            throw new AssertionError("Delete failed");
        }
        log("NOTE DELETED");
    }

    // note: verify note has been deleted
    @Then("deleted note should not exist anymore")
    public void deleted_note_should_not_exist_anymore() {
        Response response = notesApi.getNotes();
        AllureUtils.attachText("VERIFY DELETE RESPONSE", response.asPrettyString());
        List<Integer> ids = response.jsonPath().getList("data.id");
        boolean exists = ids.contains(createdNoteId);
        if (exists) {
            throw new AssertionError("Deleted note still exists");
        }
        log("DELETE VERIFIED");
    }

    // note: logout
    @And("user logs out via API")
    public void user_logs_out_via_api() {
        Response response = notesApi.logout();
        AllureUtils.attachText("LOGOUT RESPONSE", response.asPrettyString());
        if (response.getStatusCode() != 200) {
            throw new AssertionError("Logout failed");
        }
        log("LOGOUT SUCCESS");
        log("========================");
    }
}