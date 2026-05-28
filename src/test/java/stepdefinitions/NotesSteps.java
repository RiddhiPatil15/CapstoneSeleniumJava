package stepdefinitions;

import api.NotesApi;
import base.DriverFactory;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.NotesPage;
import utils.ExcelUtils;
import utils.LoggerUtils;
import utils.WaitUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static stepdefinitions.RegisterSteps.email;
import static stepdefinitions.RegisterSteps.password;

public class NotesSteps {

    private NotesPage notesPage;
    private NotesApi notesApi;
    private String tcId;
    private List<String[]> notesData = new ArrayList<>();
    private List<String> createdTitles = new ArrayList<>();
    private int apiCount;
    private String deletedNoteTitle;
    List<Map<String, String>> uiNotes = new ArrayList<>();
    private String editedTitle;
    private String editedDescription;

    private List<Map<String, String>> currentRunNotes = new ArrayList<>();

    // note: read data from notes sheet
    @When("user reads {string} from Notes sheet")
    public void user_reads_from_notes_sheet(String tc) {

        this.tcId = tc;
        notesPage = new NotesPage(DriverFactory.getDriver());
        notesApi = new NotesApi();

        LoggerUtils.initUser(email);
        LoggerUtils.info("TC START: " + tcId);

        notesData = ExcelUtils.getNotes(tcId);
        if (notesData == null || notesData.isEmpty()) {
            throw new RuntimeException("No notes found for TC: " + tcId);
        }
    }

    // note: create using ui
    @When("user creates notes from sheet data")
    public void user_creates_notes_from_sheet_data() {

        for (String[] row : notesData) {
            Map<String, String> note = new HashMap<>();

            String category = row[0];
            String title = row[1];
            String description = row[2];
            notesPage.createNote(title, description);

            LoggerUtils.info("NOTE CREATED IN UI: " + title);

            createdTitles.add(title);
            note.put("title", title);
            note.put("description", description);
            note.put("category", category);

            uiNotes.add(note);

            currentRunNotes.add(note);
        }
        LoggerUtils.info("TOTAL CREATED: " + createdTitles.size());
    }

    // note: api validate
    @Then("API should validate notes count dynamically")
    public void api_should_validate_notes_count_dynamically() {

        LoggerUtils.info("API VALIDATION STARTED");
        notesApi.authenticate(email, password);
        long start = System.currentTimeMillis();
        var response = notesApi.getNotes();

        long end = System.currentTimeMillis();
        long responseTime = end - start;

        LoggerUtils.info("API RESPONSE TIME: " + responseTime + " ms");

        if (responseTime < 2000) {
            LoggerUtils.info("API check for (<2s): PASS");
        } else {
            LoggerUtils.warn("API check for (<2s): FAIL");
        }

        int statusCode = response.getStatusCode();
        LoggerUtils.info("API RESPONSE STATUS CODE: " + statusCode);
        if (statusCode != 200) {

            throw new AssertionError("Invalid API status code: " + statusCode);
        }

        List<String> apiTitles = response.jsonPath().getList("data.title");
        apiCount = apiTitles.size();

        // note: to validate every excel note exists in API
        for (String expectedTitle : createdTitles) {
            if (!apiTitles.contains(expectedTitle)) {

                LoggerUtils.error("MISSING IN API: " + expectedTitle);
                LoggerUtils.info("API MATCH FOUND: " + expectedTitle);

                throw new AssertionError("Missing note in API: " + expectedTitle);
            }
        }
        LoggerUtils.info("UI <---> API VALIDATION SUMMARY STARTED");

        LoggerUtils.info("UI NOTES COUNT: " + createdTitles.size());
        LoggerUtils.info("API NOTES COUNT: " + apiCount);
        LoggerUtils.info("TOTAL NOTES IN API: " + apiCount);

        if (apiCount >= createdTitles.size()) {
            LoggerUtils.info("UI <---> API VALIDATION PASSED");
        } else {
            LoggerUtils.error("UI<---> API VALIDATION FAILED (COUNT MISMATCH)");
        }
        LoggerUtils.info("API VALIDATION COMPLETED");
    }

    // note: delete
    @When("user deletes one note via API")
    public void user_deletes_one_note_via_api() {

        notesApi.authenticate(email, password);

        if (currentRunNotes.isEmpty()) {
            throw new AssertionError("No notes created in current run");
        }

        Map<String, String> targetNote = currentRunNotes.get(currentRunNotes.size() - 1);
        var response = notesApi.getNotes();
        List<Map<String, Object>> apiNotes = response.jsonPath().getList("data");

        String noteId = null;
        for (Map<String, Object> n : apiNotes) {
            if (String.valueOf(n.get("title")).equals(targetNote.get("title"))) {
                noteId = String.valueOf(n.get("id"));
                break;
            }
        }
        if (noteId == null) {
            throw new AssertionError("Could not find note in API for deletion: " + targetNote.get("title"));
        }

        notesApi.deleteNote(noteId);
        deletedNoteTitle = targetNote.get("title");
        currentRunNotes.remove(currentRunNotes.size() - 1);
        LoggerUtils.info("DELETED CURRENT RUN NOTE: " + deletedNoteTitle);
    }

    // note: get notes count
    @Then("UI should show remaining notes correctly")
    public void ui_should_show_remaining_notes_correctly() {

        DriverFactory.getDriver().navigate().refresh();
        WaitUtils.handleAds(DriverFactory.getDriver());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        notesApi.authenticate(email, password);
        int expectedCount = notesApi.getNotes()
                .jsonPath()
                .getList("data.id")
                .size();
        int actualCount = notesPage.getNotesCount();

        LoggerUtils.info("EXPECTED COUNT: " + expectedCount);
        LoggerUtils.info("ACTUAL UI COUNT: " + actualCount);

        if (actualCount != expectedCount) {
            throw new AssertionError("UI count mismatch. Expected " + expectedCount + " but got " + actualCount);
        }
        boolean deletedStillPresent = notesPage.isNotePresent(deletedNoteTitle);
        if (deletedStillPresent) {
            throw new AssertionError("Deleted note still visible in UI: " + deletedNoteTitle);
        }

        LoggerUtils.info("DELETED NOTE REMOVED FROM UI");

        //note: check remaining notes
        for (Map<String, String> note : currentRunNotes) {
            String title = note.get("title");
            if (!notesPage.isNotePresent(title)) {
                throw new AssertionError("Missing note in UI: " + title);
            }
        }
        LoggerUtils.info("UI VALIDATION SUCCESSFUL");
    }

    @When("user deletes one note via UI")
    public void user_deletes_one_note_via_ui() {
        notesPage = new NotesPage(DriverFactory.getDriver());
        notesPage.deleteNote();
    }

    // note: edit first note

    @When("user edits first added note")
    public void user_edits_first_added_note() {

        if (currentRunNotes.isEmpty()) {
            LoggerUtils.warn("NO NOTES IN CURRENT RUN TO EDIT");
            return;
        }

        Map<String, String> targetNote = currentRunNotes.get(0);

        String originalTitle = targetNote.get("title");
        String originalDesc = targetNote.get("description");

        editedTitle = originalTitle + " NEW!";
        editedDescription = originalDesc + " NEW!";

        notesPage.editNoteByTitle(
                originalTitle,
                editedTitle,
                editedDescription
        );

        LoggerUtils.info("EDITED CURRENT RUN FIRST NOTE: " + originalTitle);
    }

    @Then("API should reflect edited note details")
    public void api_should_reflect_edited_note_details() {

        if (editedTitle == null) {
            LoggerUtils.warn("EDIT VALIDATION SKIPPED - NO NOTE WAS EDITED");
            return;
        }

        notesApi.authenticate(email, password);
        var response = notesApi.getNotes();

        List<Map<String, Object>> notes = response.jsonPath().getList("data");
        boolean found = false;
        for (Map<String, Object> note : notes) {
            String title = String.valueOf(note.get("title"));
            String desc = String.valueOf(note.get("description"));
            if (title.equals(editedTitle) &&
                    desc.equals(editedDescription)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new AssertionError("Edited note not found in API with correct title + description");
        }

        LoggerUtils.info("API EDIT VALIDATION SUCCESSFUL");
    }

    @When("user deletes all notes before logout")
    public void delete_all_notes_before_logout() {

        notesApi.authenticate(email, password);

        List<Map<String, Object>> notes = notesApi.getNotes().jsonPath().getList("data");
        for (Map<String, Object> note : notes) {
            String id = String.valueOf(note.get("id"));
            notesApi.deleteNote(id);
        }
        LoggerUtils.info("ALL NOTES CLEANED UP BEFORE LOGOUT");
    }
}