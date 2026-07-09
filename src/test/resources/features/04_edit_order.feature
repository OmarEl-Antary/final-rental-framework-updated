Feature: Edit Order Flow
  As a logged-in user
  I want to edit my latest order by adding a new product
  So that I can update my rental items

  Background:
    Given the user is logged in with phone "1020416304" and OTP "1111"

  Scenario: Edit order from order options menu (3 dots)
    When the user clicks on profile link
    And the user clicks on my orders
    And the user clicks order options menu
    And the user clicks edit order from options
    And clicks add product button
    And clicks the first product
    And clicks add to order button
    And clicks complete order button
    And clicks complete order address button
    Then the edit order button should be visible