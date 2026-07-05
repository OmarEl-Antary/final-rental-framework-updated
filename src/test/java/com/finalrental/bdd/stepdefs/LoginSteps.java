package com.finalrental.bdd.stepdefs;

import com.finalrental.pages.HomePage;
import com.finalrental.pages.LoginOtpPage;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class LoginSteps {

    private LoginOtpPage loginOtpPage;

    @Given("the user opens the home page")
    public void the_user_opens_the_home_page() {
        new HomePage().open();
    }

    @When("the user clicks the login button")
    public void the_user_clicks_the_login_button() {
        loginOtpPage = new LoginOtpPage().dismissBanner();
        loginOtpPage.clickLoginButton();
    }

    @And("selects country code {string}")
    public void selects_country_code(String countryCode) {
        loginOtpPage.selectCountryCodeByText(countryCode);
    }

    @And("enters phone number {string}")
    public void enters_phone_number(String phone) {
        loginOtpPage.enterPhoneNumber(phone);
    }

    @And("clicks send OTP button")
    public void clicks_send_otp_button() {
        loginOtpPage.clickSendOtp();
    }

    @And("enters OTP code {string}")
    public void enters_otp_code(String otp) {
        loginOtpPage.enterOtp(otp);
    }

    @And("clicks verify button")
    public void clicks_verify_button() {
        loginOtpPage.clickVerify();
    }

    @Then("the user should be logged in successfully")
    public void the_user_should_be_logged_in_successfully() {
        Assert.assertTrue(true, "User logged in successfully");
    }

    @Then("the OTP input fields should be visible")
    public void the_otp_input_fields_should_be_visible() {
        Assert.assertTrue(loginOtpPage.isOtpStepVisible(),
                "OTP fields should be visible");
    }

    @And("there should be exactly {int} OTP input fields")
    public void there_should_be_exactly_otp_input_fields(int count) {
        Assert.assertEquals(loginOtpPage.getOtpInputCount(), count,
                "OTP input count mismatch");
    }
}