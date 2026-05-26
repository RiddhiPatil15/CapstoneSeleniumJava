package stepdefinitions;

import api.NotesApi;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.ConfigReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APISteps {

    private final String BASE_URL =
            ConfigReader.getProperty("apiBaseUrl");

    private final NotesApi notesApi =
            new NotesApi();

    private String token;

    private String email;
    private String password;

    private String createdNoteId;
    private String createdNoteTitle;

    // ---------------- LOGGER ----------------
    private void log(String message) {

        try {

            new File("UserLogs").mkdirs();

            FileWriter writer =
                    new FileWriter(
                            "UserLogs/API_Flow_Log.txt",
                            true
                    );

            writer.write(message + "\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- REGISTER ----------------
    @Given("user registers via API with {string} {string} {string}")
    public void user_registers_via_api_with(
            String name,
            String email,
            String password
    ) {

        this.email = email;
        this.password = password;

        Map<String, String> body =
                new HashMap<>();

        body.put("name", name);
        body.put("email", email);
        body.put("password", password);

        Response response =
                RestAssured
                        .given()
                        .baseUri(BASE_URL)
                        .contentType("application/json")
                        .body(body)
                        .post("/users/register");

        int statusCode =
                response.getStatusCode();

        if (statusCode == 201) {

            log("========================");
            log("REGISTER SUCCESS");
            log("EMAIL: " + email);

        } else if (statusCode == 409) {

            log("========================");
            log("USER ALREADY EXISTS");
            log("EMAIL: " + email);

        } else {

            throw new AssertionError(
                    "Registration failed: "
                            + statusCode
            );
        }
    }

    // ---------------- LOGIN ----------------
    @When("user logs in via API with {string} {string}")
    public void user_logs_in_via_api_with(
            String email,
            String password
    ) {

        this.email = email;
        this.password = password;

        notesApi.authenticate(email, password);

        log("LOGIN SUCCESS");
    }

    // ---------------- CREATE NOTE ----------------
    @When("user creates note via API with {string} {string} {string}")
    public void user_creates_note_via_api_with(
            String category,
            String title,
            String description
    ) {

        createdNoteTitle = title;

        Map<String, String> body =
                new HashMap<>();

        body.put("title", title);
        body.put("description", description);
        body.put("category", category);

        Response response =
                RestAssured
                        .given()
                        .baseUri(BASE_URL)
                        .header(
                                "x-auth-token",
                                getToken()
                        )
                        .contentType("application/json")
                        .body(body)
                        .post("/notes");

        if (response.getStatusCode() != 200 &&
                response.getStatusCode() != 201) {

            throw new AssertionError(
                    "Create note failed"
            );
        }

        createdNoteId =
                response.jsonPath()
                        .getString("data.id");

        log("NOTE CREATED");
        log("TITLE: " + title);
        log("CATEGORY: " + category);
    }

    // ---------------- VERIFY NOTE ----------------
    @Then("note should exist in API response")
    public void note_should_exist_in_api_response() {

        Response response =
                notesApi.getNotes();

        List<String> titles =
                response.jsonPath()
                        .getList("data.title");

        if (!titles.contains(createdNoteTitle)) {

            throw new AssertionError(
                    "Created note not found"
            );
        }

        log("NOTE VERIFIED IN GET RESPONSE");
    }

    // ---------------- DELETE NOTE ----------------
    @When("user deletes created note via API")
    public void user_deletes_created_note_via_api() {

        Response response =
                notesApi.deleteNote(createdNoteId);

        if (response.getStatusCode() != 200) {

            throw new AssertionError(
                    "Delete failed"
            );
        }

        log("NOTE DELETED");
    }

    // ---------------- VERIFY DELETE ----------------
    @Then("deleted note should not exist anymore")
    public void deleted_note_should_not_exist_anymore() {

        Response response =
                notesApi.getNotes();

        List<String> titles =
                response.jsonPath()
                        .getList("data.title");

        if (titles.contains(createdNoteTitle)) {

            throw new AssertionError(
                    "Deleted note still exists"
            );
        }

        log("DELETE VERIFIED");
    }

    // ---------------- LOGOUT ----------------
    @And("user logs out via API")
    public void user_logs_out_via_api() {

        Response response =
                RestAssured
                        .given()
                        .baseUri(BASE_URL)
                        .header(
                                "x-auth-token",
                                getToken()
                        )
                        .delete("/users/logout");

        if (response.getStatusCode() != 200) {

            throw new AssertionError(
                    "Logout failed"
            );
        }

        log("LOGOUT SUCCESS");
        log("========================");
    }

    // ---------------- TOKEN FETCH ----------------
    private String getToken() {

        Response response =
                RestAssured
                        .given()
                        .baseUri(BASE_URL)
                        .contentType("application/json")
                        .body(
                                Map.of(
                                        "email", email,
                                        "password", password
                                )
                        )
                        .post("/users/login");

        token =
                response.jsonPath()
                        .getString("data.token");

        return token;
    }
}