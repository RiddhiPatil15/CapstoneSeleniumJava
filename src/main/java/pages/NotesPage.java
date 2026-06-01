package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.WaitUtils;

import java.util.List;

public class NotesPage {

    private WebDriver driver;
    public NotesPage(WebDriver driver) {
        this.driver = driver;
    }

    // locators
    private By addNoteBtn = By.cssSelector("[data-testid='add-new-note']");
    private By titleInput = By.cssSelector("[data-testid='note-title']");
    private By descInput = By.cssSelector("[data-testid='note-description']");
    private By saveBtn = By.cssSelector("[data-testid='note-submit']");
    private By noteItems = By.cssSelector("[data-testid='note-card']");
    private By deleteBtn = By.cssSelector("[data-testid='note-delete']");
    private By confirmDeleteBtn = By.cssSelector("[data-testid='note-delete-confirm']");
    private By editBtn = By.cssSelector("[data-testid='note-edit']");
    private By updateBtn = By.cssSelector("[data-testid='note-submit']");

    // create note
    public void createNote(String title, String description) {
        WaitUtils.handleAds(driver);
        WaitUtils.scrollAndClick(driver, addNoteBtn);
        WaitUtils.handleAds(driver);

        // if notes form did not open
        if (driver.findElements(titleInput).isEmpty()) {
            WaitUtils.handleAds(driver);
            WaitUtils.scrollAndClick(driver, addNoteBtn);
        }
        WaitUtils.HealType(driver, titleInput, title);
        WaitUtils.HealType(driver, descInput, description);
        WaitUtils.scrollAndClick(driver, saveBtn);
        WaitUtils.waitForInvisible(driver, titleInput);
        WaitUtils.handleAds(driver);
    }

    // note: count note
    public int getNotesCount() {

        WaitUtils.handleAds(driver);
        // note: helps in case of unstable DOM rendering
        WaitUtils.waitForCondition(driver, d -> {
            try {
                return d.findElements(noteItems).size() >= 0;
                //return !d.findElements(noteItems).isEmpty();
            } catch (Exception e) {
                return false;
            }
        });

        int count = driver.findElements(noteItems).size();
        System.out.println("FOUND NOTES IN UI: " + count);
        return count;
    }

    // note: check note present
    public boolean isNotePresent(String title) {
        WaitUtils.handleAds(driver);
        List<WebElement> notes = driver.findElements(noteItems);
        for (WebElement note : notes) {
            String noteText = note.getText();
            if (noteText.contains(title)) {
                return true;
            }
        }
        return false;
    }

    public void deleteNote() {
        WaitUtils.handleAds(driver);
        WaitUtils.scrollAndClick(driver, deleteBtn);
        WaitUtils.handleAds(driver);
        WaitUtils.scrollAndClick(driver, confirmDeleteBtn);

        WaitUtils.waitForCondition(driver, d -> {
            try {
                return d.findElements(noteItems).size() >= 0;
            } catch (Exception e) {
                return false;
            }
        });

        WaitUtils.handleAds(driver);
    }

    public void editNoteByTitle(String targetTitle, String updatedTitle, String updatedDescription) {
        WaitUtils.handleAds(driver);
        List<WebElement> notes = driver.findElements(noteItems);
        for (WebElement note : notes) {
            if (note.getText().contains(targetTitle)) {
                WaitUtils.handleAds(driver);
                WebElement editElement = note.findElement(editBtn);
                try {
                    editElement.click();
                } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                    WaitUtils.handleAds(driver);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editElement);
                }
                break;
            }
        }
        WebElement titleField = WaitUtils.waitForVisible(driver, titleInput);
        WebElement descField = WaitUtils.waitForVisible(driver, descInput);

        titleField.clear();
        titleField.sendKeys(updatedTitle);

        descField.clear();
        descField.sendKeys(updatedDescription);

        WaitUtils.scrollAndClick(driver, updateBtn);
        WaitUtils.waitForInvisible(driver, titleInput);

        WaitUtils.handleAds(driver);
    }

}