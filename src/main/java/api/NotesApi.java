package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.ConfigReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesApi {

    private static final String BASE_URL = ConfigReader.getProperty("apiBaseUrl");
    private String token;

    // note: authenticate
    public void authenticate(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        Response response = RestAssured
                .given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(body)
                .post("/users/login");

        token = response
                .jsonPath()
                .getString("data.token");

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Failed to generate auth token");
        }
    }

    // note: header
    private Map<String, String> headers() {
        if (token == null) {
            throw new RuntimeException("Token missing. Authenticate first.");
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("x-auth-token", token);
        return headers;
    }

    public Response createNote(String title, String description, String category) {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description);
        body.put("category", category);

        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .headers(headers())
                .contentType("application/json")
                .body(body)
                .post("/notes");
    }

    // note: get note
    public Response getNotes() {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .headers(headers())
                .get("/notes");
    }

    // note: delete note
    public Response deleteNote(String noteId) {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .headers(headers())
                .delete("/notes/" + noteId);
    }

    // note: logout
    public Response logout() {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .headers(headers())
                .delete("/users/logout");
    }
}