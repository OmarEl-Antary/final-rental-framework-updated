package com.finalrental.bdd.stepdefs;

import com.finalrental.config.DriverFactory;
import com.finalrental.data.TestContext;
import com.finalrental.pages.*;
import io.cucumber.java.en.*;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class RegisterSteps {

    private static final Logger log =
            LoggerFactory.getLogger(RegisterSteps.class);

    private RegisterPage registerPage;
    private String phoneNumber;

    @When("the user clicks the login register button")
    public void the_user_clicks_the_login_register_button() {
        registerPage = new RegisterPage();
        try {
            JavascriptExecutor js =
                    (JavascriptExecutor) DriverFactory.getDriver();
            js.executeScript("document.body.click();");
        } catch (Exception ignored) {}
        registerPage.clickLoginRegisterButton();
        log.info("✔ تم فتح الـ popup");
    }

    @And("selects country code for register {string}")
    public void selects_country_code_for_register(String countryCode) {
        registerPage.selectCountryCode(countryCode);
        log.info("✔ تم اختيار كود الدولة: {}", countryCode);
    }

    @And("enters a random Egyptian phone number")
    public void enters_a_random_egyptian_phone_number() {
        phoneNumber = RegisterPage.generateEgyptianPhone();
        TestContext.setRegisteredPhone(phoneNumber);
        registerPage.enterPhone(phoneNumber);
        log.info("✔ تم إدخال رقم الهاتف: {}", phoneNumber);
    }

    @And("clicks send OTP button for register")
    public void clicks_send_otp_button_for_register() {
        registerPage.clickSendOtp();
        log.info("✔ تم إرسال الـ OTP");
    }

    @And("enters OTP code for register {string}")
    public void enters_otp_code_for_register(String otp) {
        registerPage.enterOtp(otp);
        log.info("✔ تم إدخال الـ OTP: {}", otp);
    }

    @And("clicks verify button for register")
    public void clicks_verify_button_for_register() {
        registerPage.clickVerify();
        log.info("✔ تم التحقق");
    }

    @Then("the registration form should be visible")
    public void the_registration_form_should_be_visible() {
        Assert.assertTrue(registerPage.isRegistrationFormVisible(),
                "Registration form should be visible");
        log.info("✔ فورم التسجيل ظهر");
    }

    @When("the user enters name {string}")
    public void the_user_enters_name(String name) {
        registerPage.enterName(RegisterPage.generateRandomName());
        log.info("✔ تم إدخال الاسم");
    }

    @And("the user enters email with timestamp")
    public void the_user_enters_email_with_timestamp() {
        registerPage.enterEmail(RegisterPage.generateRandomEmail());
        log.info("✔ تم إدخال الإيميل");
    }

    @And("the user enters instagram {string}")
    public void the_user_enters_instagram(String instagram) {
        registerPage.enterInstagram(RegisterPage.generateRandomInstagram());
        log.info("✔ تم إدخال الإنستجرام");
    }

    @And("the user accepts terms and conditions")
    public void the_user_accepts_terms_and_conditions() {
        registerPage.acceptTerms();
        log.info("✔ تم الموافقة على الشروط");
    }

    @And("the user clicks complete registration")
    public void the_user_clicks_complete_registration() {
        registerPage.clickRegister();
        log.info("✔ تم الضغط على إكمال التسجيل");
    }

    @Then("the registration should be successful")
    public void the_registration_should_be_successful() {
        Assert.assertTrue(registerPage.isRegistrationSuccessful(),
                "Registration should be successful");
        log.info("تم التسجيل بنجاح!");
    }
}