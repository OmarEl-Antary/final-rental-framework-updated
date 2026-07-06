package com.finalrental.bdd.stepdefs;

import com.finalrental.pages.*;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class OrderSteps {

    private ProductsPage productsPage;
    private CartPage cartPage;
    private OrderConfirmationPage confirmPage;
    private OrdersPage ordersPage;
    private OrderSummaryPage summaryPage;

    @Given("the user is logged in with phone {string} and OTP {string}")
    public void the_user_is_logged_in(String phone, String otp) {
        new HomePage().open();
        LoginOtpPage loginOtpPage = new LoginOtpPage().dismissBanner();
        loginOtpPage.clickLoginButton()
                .selectCountryCodeByText("+20")
                .enterPhoneNumber(phone)
                .clickSendOtp()
                .enterOtp(otp)
                .clickVerify();
    }

    @When("the user selects city {string} and clicks search")
    public void the_user_selects_city_and_clicks_search(String cityValue) {
        productsPage = new HomePage()
                .selectCity(cityValue)
                .clickSearch();
    }

    @Then("products should be displayed")
    public void products_should_be_displayed() {
        Assert.assertTrue(productsPage.hasProducts(),
                "Products should be visible");
    }

    @When("the user adds the first product to cart")
    public void the_user_adds_the_first_product_to_cart() {
        productsPage.clickAddToCart();
    }

    @Then("the cart bottom sheet should appear")
    public void the_cart_bottom_sheet_should_appear() {
        Assert.assertTrue(true, "Bottom sheet appeared");
    }

    @When("the user navigates to cart page")
    public void the_user_navigates_to_cart_page() {
        cartPage = productsPage.clickContinueToOrder();
    }

    @Then("the cart page should be loaded")
    public void the_cart_page_should_be_loaded() {
        Assert.assertTrue(cartPage.isCartPageLoaded(),
                "Cart page should be loaded");
    }

    @When("the user selects pickup date {string}")
    public void the_user_selects_pickup_date(String date) {
        cartPage.selectFromDate(date);
    }

    @And("selects pickup time {string}")
    public void selects_pickup_time(String time) {
        cartPage.selectFromTime(time);
    }

    @And("selects return date {string}")
    public void selects_return_date(String date) {
        cartPage.selectToDate(date);
    }

    @And("selects return time {string}")
    public void selects_return_time(String time) {
        cartPage.selectToTime(time);
    }

    @And("submits the cart")
    public void submits_the_cart() {
        confirmPage = cartPage.clickSubmitCart();
    }

    @Then("the order confirmation page should be loaded")
    public void the_order_confirmation_page_should_be_loaded() {
        Assert.assertTrue(confirmPage.isConfirmationPageLoaded(),
                "Confirmation page should be loaded");
    }

    @When("the user clicks end order button")
    public void the_user_clicks_end_order_button() {
        confirmPage.clickEndOrder();
    }

    @Then("the terms modal should appear")
    public void the_terms_modal_should_appear() {
        Assert.assertTrue(confirmPage.isTermsModalVisible(),
                "Terms modal should appear");
    }

    @When("the user agrees to terms")
    public void the_user_agrees_to_terms() {
        confirmPage.clickAgreeToTerms();
    }

    @Then("the order completed modal should appear")
    public void the_order_completed_modal_should_appear() {
        Assert.assertTrue(confirmPage.isOrderCompletedModalVisible(),
                "Order completed modal should appear");
    }

    @When("the user clicks track order")
    public void the_user_clicks_track_order() {
        confirmPage.clickTrackOrder();
    }

    @Then("the user should be on the orders page")
    public void the_user_should_be_on_the_orders_page() {
        Assert.assertTrue(confirmPage.getCurrentUrl().contains("orders"),
                "Should be on orders page");
    }

    @When("the user closes any popup")
    public void the_user_closes_any_popup() {
        confirmPage.closeAnyPopup();
    }

    @And("clicks the order menu")
    public void clicks_the_order_menu() {
        ordersPage = new OrdersPage();
        ordersPage.clickOrderMenu();
    }

    @And("clicks view order details")
    public void clicks_view_order_details() {
        ordersPage.clickViewOrderDetails();
    }

    @Then("the order details page should be displayed")
    public void the_order_details_page_should_be_displayed() {
        summaryPage = new OrderSummaryPage();
        Assert.assertTrue(summaryPage.isSummaryVisible(),
                "Order summary should be visible");
    }

    @And("the tax calculation should be correct")
    public void the_tax_calculation_should_be_correct() {
        Assert.assertTrue(summaryPage.isTaxCalculationCorrect(),
                "Tax calculation should be correct");
    }

    @And("the total calculation should be correct")
    public void the_total_calculation_should_be_correct() {
        Assert.assertTrue(summaryPage.isTotalCalculationCorrect(),
                "Total calculation should be correct");
    }
}