package stepdefinitions;

import api.NotesApi;
import base.DriverFactory;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
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
        deletedNoteTitle = notesApi.getLatestNoteTitle();
        String noteId = notesApi.getFirstNoteId();

        LoggerUtils.warn("DELETING NOTE: " + deletedNoteTitle);

        var response = notesApi.deleteNote(noteId);
        if (response.getStatusCode() != 200) {

            throw new AssertionError("Delete failed with status: " + response.getStatusCode());
        }
        LoggerUtils.info("NOTE DELETED SUCCESSFULLY");
    }

    // note: get notes count
    @Then("UI should show remaining notes correctly")
    public void ui_should_show_remaining_notes_correctly() {
        //DriverFactory.getDriver().navigate().refresh();
        WebDriver driver = DriverFactory.getDriver();
        driver.navigate().refresh();
        WaitUtils.handleAds(DriverFactory.getDriver());

        // note: avoid race condition
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        int actualCount = notesPage.getNotesCount();
        int expectedCount = apiCount - 1;

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
        for (String title : createdTitles) {

            if (title.equals(deletedNoteTitle)) {
                continue;
            }
            if (!notesPage.isNotePresent(title)) {
                throw new AssertionError("Expected note missing in UI: " + title);
            }
            LoggerUtils.info("UI VERIFIED NOTE: " + title);
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
        if (createdTitles.size() <= 1) {
            LoggerUtils.warn("NO REMAINING NOTES AVAILABLE FOR EDIT");
            return;
        }

        // find first non-deleted note
        Map<String, String> noteToEdit = null;
        for (Map<String, String> note : uiNotes) {
            if (!note.get("title").equals(deletedNoteTitle)) {
                noteToEdit = note;
                break;
            }
        }
        if (noteToEdit == null) {
            LoggerUtils.warn("NO VALID NOTE FOUND FOR EDIT");
            return;
        }

        String originalTitle = noteToEdit.get("title");
        String originalDescription = noteToEdit.get("description");
        editedTitle = originalTitle + " NEW!";
        editedDescription = originalDescription + " NEW!";

        notesPage.editFirstNote(editedTitle, editedDescription);
        LoggerUtils.info("NOTE EDITED");
        LoggerUtils.info("OLD TITLE: " + originalTitle);
        LoggerUtils.info("NEW TITLE: " + editedTitle);
        LoggerUtils.info("OLD DESCRIPTION: " + originalDescription);

        LoggerUtils.info("NEW DESCRIPTION: " + editedDescription);
    }

    // note: api validate edit
    @Then("API should reflect edited note details")
    public void api_should_reflect_edited_note_details() {

        if (editedTitle == null) {
            LoggerUtils.warn("EDIT VALIDATION SKIPPED - NO NOTE WAS EDITED");
            return;
        }
        notesApi.authenticate(email, password);
        var response = notesApi.getNotes();
        List<String> titles = response.jsonPath().getList("data.title");
        List<String> descriptions = response.jsonPath().getList("data.description");

        if (!titles.contains(editedTitle)) {
            throw new AssertionError("Edited title not found in API");
        }
        if (!descriptions.contains(editedDescription)) {
            throw new AssertionError("Edited description not found in API");
        }
        LoggerUtils.info("API EDIT VALIDATION SUCCESSFUL");
    }
}