Feature: Address Management
  As a logged-in user
  I want to add and edit addresses
  So that I can manage my delivery locations

  Background:
    Given the user is logged in with phone "1020416304" and OTP "1111"

  Scenario: Add a new address in Jeddah
    When the user clicks on profile link
    And the user clicks on my addresses
    And the user clicks add new address
    And the user enters a random address name
    And the user selects city "فرع جدة"
    And the user enters Jeddah address details
    And the user searches on map with address details
    And the user clicks submit address
    Then the address should be added successfully

  Scenario: Edit the last address
    When the user clicks on profile link
    And the user clicks on my addresses
    And the user clicks edit last address
    And the user enters a random address name
    And the user enters Jeddah address details
    And the user searches on map with address details
    And the user clicks submit edit address
    Then the address should be updated successfully