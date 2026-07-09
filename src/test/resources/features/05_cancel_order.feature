Feature: Cancel Order Flow
  As a logged-in user
  I want to cancel my latest order
  So that I can stop an unwanted rental

  Background:
    Given the user is logged in with phone "1020416304" and OTP "1111"

  Scenario: Cancel order from order details page
    When the user clicks on profile link
    And the user clicks on my orders
    And the user clicks order options menu
    And the user clicks view order from options
    And the user clicks cancel order button
    And the user clicks confirm cancel
    And the user enters cancel reason "لم يعد لي الحاجه للطلب"
    And the user clicks confirm cancel
    Then the order should be cancelled successfully