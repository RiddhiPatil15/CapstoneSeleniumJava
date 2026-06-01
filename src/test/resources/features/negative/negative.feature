Feature: Negative Validation Flow

@negative @register
Scenario Outline: Validate negative registration scenarios

  Given user opens the application
  When negative user reads "<TC_ID>" from RegisterNegative sheet
  And user enters invalid registration details
  And user submits invalid registration form
  Then proper registration error should be shown

Examples:
  | TC_ID |
  | TC_NEG_01 |
  | TC_NEG_02 |
  | TC_NEG_03 |
  | TC_NEG_04 |
  | TC_NEG_05 |


@negative @login
Scenario Outline: Validate negative login scenarios

  Given user opens the application
  When negative user reads "<TC_ID>" from LoginNegative sheet
  And user enters invalid login details
  Then proper login error should be shown

Examples:
  | TC_ID |
  | TC_NEG_06 |
  | TC_NEG_07 |
  | TC_NEG_08 |
  | TC_NEG_09 |

@negative @notes
Scenario Outline: Validate negative notes scenarios using valid login

  Given user opens the application and navigates to login page
  When user logs in with valid credentials for notes
  Then dashboard should be visible for notes flow
  When user reads "<TC_ID>" from NotesNegative sheet
  And user performs invalid notes actions
  Then proper notes error should be shown

Examples:
  | TC_ID     |
  | TC_NEG_10 |
  | TC_NEG_11 |
  | TC_NEG_12 |
  | TC_NEG_13 |


@negative @api
Scenario Outline: Validate negative API scenarios

  When API negative user reads "<TC_ID>" from ApiNegative sheet
  And user performs invalid API action
  Then proper API error should be returned

Examples:
  | TC_ID |
  | TC_API_NEG_01 |
  | TC_API_NEG_02 |
  | TC_API_NEG_03 |
  | TC_API_NEG_04 |
  | TC_API_NEG_05 |
  | TC_API_NEG_06 |
  | TC_API_NEG_07 |
  | TC_API_NEG_08 |
  | TC_API_NEG_09 |
