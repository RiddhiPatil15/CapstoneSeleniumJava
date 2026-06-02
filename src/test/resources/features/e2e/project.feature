Feature: End to End User Flow (Register --> Login --> Notes --> API --> Logout)

@e2e
Scenario Outline: Complete user journey from registration to logout

  Given user opens the application
  When user reads "<UserTestCaseID>" from Users sheet
  And user enters registration details
  And user submits the registration form
  Then user should see registration success message

  When user logs in with stored credentials
  Then user dashboard should be visible

  When user reads "<NotesTestCaseID>" from Notes sheet
  And user creates notes from sheet data

  Then API should validate notes count dynamically

  When user deletes one note via API
  Then UI should show remaining notes correctly

  When user edits first added note
  Then API should reflect edited note details

  And user deletes all notes before logout
  And user logs out

Examples:
  | UserTestCaseID | NotesTestCaseID |
  | TC_01          | TC_01           |
  | TC_02          | TC_02           |
  | TC_04          | TC_04           |
