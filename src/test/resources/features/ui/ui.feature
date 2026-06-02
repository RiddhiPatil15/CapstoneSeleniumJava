Feature: UI End to End Flow (Register --> Login --> Notes --> Delete --> Logout)

@ui
Scenario Outline: Complete UI journey using UI only flow

  Given user opens the application

  When user reads "<UserTestCaseID>" from Users sheet
  And user enters registration details
  And user submits the registration form
  Then user should see registration success message

  When user logs in with stored credentials
  Then user dashboard should be visible

  When user reads "<NotesTestCaseID>" from Notes sheet
  And user creates notes from sheet data

  When user deletes one note via UI
  And user logs out

Examples:
  | UserTestCaseID | NotesTestCaseID |
  | TC_03          | TC_03           |