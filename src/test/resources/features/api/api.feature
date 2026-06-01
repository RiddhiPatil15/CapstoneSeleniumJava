Feature: Pure API Notes Flow

  @api
  Scenario: Complete API lifecycle
    Given user registers via API with "API User" "apitest123@yopmail.com" "Test@123"
    When user logs in via API with "apitest123@yopmail.com" "Test@123"
    And user creates note via API with "Work" "API Note" "Created using RestAssured"
    Then note should exist in API response
    When user deletes created note via API
    Then deleted note should not exist anymore
    And user logs out via API