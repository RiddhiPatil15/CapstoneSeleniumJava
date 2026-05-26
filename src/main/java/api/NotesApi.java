package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.ConfigReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesApi {

    private static final String BASE_URL =
            ConfigReader.getProperty("apiBaseUrl");

    private String token;

    // ---------------- AUTHENTICATE ----------------
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

    // ---------------- COMMON HEADERS ----------------
    private Map<String, String> headers() {

        if (token == null) {
            throw new RuntimeException(
                    "Token missing. Authenticate first."
            );
        }

        Map<String, String> headers = new HashMap<>();

        headers.put("x-auth-token", token);

        return headers;
    }

    // ---------------- CREATE NOTE ----------------
    public Response createNote(String title, String description) {

        Map<String, String> body = new HashMap<>();

        body.put("title", title);
        body.put("description", description);

        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .headers(headers())
                .contentType("application/json")
                .body(body)
                .post("/notes");
    }

    // ---------------- GET NOTES ----------------
    public Response getNotes() {

        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .headers(headers())
                .get("/notes");
    }

    // ---------------- DELETE NOTE ----------------
    public Response deleteNote(String noteId) {

        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .headers(headers())
                .delete("/notes/" + noteId);
    }

    // ---------------- NOTES COUNT ----------------
    public int getNotesCount() {

        return getNotes()
                .jsonPath()
                .getList("data")
                .size();
    }

    // ---------------- FIRST NOTE ID ----------------
    public String getFirstNoteId() {

        List<String> ids = getNotes()
                .jsonPath()
                .getList("data.id");

        if (ids == null || ids.isEmpty()) {

            throw new RuntimeException(
                    "No note IDs found"
            );
        }

        return ids.get(0);
    }

    // ---------------- FIRST NOTE TITLE ----------------
    public String getLatestNoteTitle() {

        List<String> titles = getNotes()
                .jsonPath()
                .getList("data.title");

        if (titles == null || titles.isEmpty()) {

            throw new RuntimeException(
                    "No note titles found"
            );
        }

        return titles.get(0);
    }

    // ---------------- UPDATE NOTE ----------------
    public Response updateNote(
            String noteId,
            String title,
            String description
    ) {

        Map<String, String> body = new HashMap<>();

        body.put("title", title);
        body.put("description", description);

        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .headers(headers())
                .contentType("application/json")
                .body(body)
                .put("/notes/" + noteId);
    }
}