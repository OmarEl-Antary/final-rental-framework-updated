Feature: OTP Login Flow
  As a user
  I want to login using my phone number and OTP
  So that I can access my account

  Scenario: Successful login with valid phone and OTP
    Given the user opens the home page
    When the user clicks the login button
    And selects country code "+20"
    And enters phone number "1020416304"
    And clicks send OTP button
    And enters OTP code "1111"
    And clicks verify button
    Then the user should be logged in successfully

  Scenario: OTP fields appear after sending OTP
    Given the user opens the home page
    When the user clicks the login button
    And selects country code "+20"
    And enters phone number "1020416304"
    And clicks send OTP button
    Then the OTP input fields should be visible
    And there should be exactly 4 OTP input fields