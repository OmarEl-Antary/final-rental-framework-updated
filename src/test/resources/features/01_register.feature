Feature: Register New User
  As a new user
  I want to register a new account with a random Egyptian phone number
  So that I can access the platform

  Scenario: Register a new user with random Egyptian phone number
    Given the user opens the home page
    When the user clicks the login register button
    And selects country code for register "+20"
    And enters a random Egyptian phone number
    And clicks send OTP button for register
    And enters OTP code for register "1111"
    And clicks verify button for register
    Then the registration form should be visible

    When the user enters name "عمر آسام العماري"
    And the user enters email with timestamp
    And the user enters instagram "@omarosama"
    And the user accepts terms and conditions
    And the user clicks complete registration
    Then the registration should be successful