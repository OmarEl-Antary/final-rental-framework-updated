Feature: Full Order Flow
  As a logged-in user
  I want to search for products, add to cart, and place an order
  So that I can rent equipment successfully

  Background:
    Given the user is logged in with phone "1020416304" and OTP "1111"

  Scenario: Complete order flow from search to order confirmation
    Given the user opens the home page
    When the user selects city "6" and clicks search
    Then products should be displayed

    When the user adds the first product to cart
    Then the cart bottom sheet should appear

    When the user navigates to cart page
    Then the cart page should be loaded

    When the user selects dynamic pickup date
    And selects pickup time "7:00"
    And the user selects dynamic return date
    And selects return time "7:00"
    And submits the cart
    And the user enters identity number
    And the user confirms the complete order

    When the user agrees to terms
    Then the order completed modal should appear

    When the user clicks track order
    Then the user should be on the orders page

    When the user closes any popup
    And clicks the order menu
    And clicks view order details
    Then the order details page should be displayed

    And the tax calculation should be correct
    And the total calculation should be correct