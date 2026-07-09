package com.finalrental.tests;

import com.finalrental.data.TestContext;
import com.finalrental.pages.HomePage;
import com.finalrental.pages.LoginOtpPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginOtpTest extends BaseTest {

    private LoginOtpPage loginOtpPage;

    private static final String COUNTRY_CODE_TEXT = "+20";
    private static final String OTP_CODE          = "1111";

    @BeforeMethod
    public void openHomePage() {
        new HomePage().open();
        loginOtpPage = new LoginOtpPage().dismissBanner();
    }

    @Test(groups = {"smoke", "regression"},
            description = "Verify entering phone number and clicking Send OTP shows the OTP fields")
    public void sendOtpShowsOtpInputFields() {
        String phone = TestContext.getRegisteredPhone();
        log.info("Using phone: {}", phone);

        loginOtpPage.clickLoginButton()
                .selectCountryCodeByText(COUNTRY_CODE_TEXT)
                .enterPhoneNumber(phone)
                .clickSendOtp();

        assertThat(loginOtpPage.isOtpStepVisible())
                .as("OTP input fields should appear after sending the OTP")
                .isTrue();

        assertThat(loginOtpPage.getOtpInputCount())
                .as("There should be exactly 4 OTP input fields")
                .isEqualTo(4);
    }

    @Test(groups = {"smoke", "regression"},
            description = "Full login flow: phone number -> OTP -> verify")
    public void fullPhoneOtpLoginFlow() {
        String phone = TestContext.getRegisteredPhone();
        log.info("Using phone: {}", phone);

        loginOtpPage.clickLoginButton()
                .selectCountryCodeByText(COUNTRY_CODE_TEXT)
                .enterPhoneNumber(phone)
                .clickSendOtp();

        assertThat(loginOtpPage.isOtpStepVisible())
                .as("OTP fields should be visible before entering the code")
                .isTrue();

        loginOtpPage.enterOtp(OTP_CODE);

        assertThat(loginOtpPage.isVerifyButtonEnabled())
                .as("Verify button should be enabled after entering all OTP digits")
                .isTrue();

        loginOtpPage.clickVerify();

        log.info("OTP verification submitted successfully.");
    }
}