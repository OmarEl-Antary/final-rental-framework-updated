package com.finalrental.tests;

import com.finalrental.pages.HomePage;
import com.finalrental.pages.LoginOtpPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginOtpTest extends BaseTest {

    private LoginOtpPage loginOtpPage;

    private static final String COUNTRY_CODE_TEXT = "+20";
    private static final String PHONE_NUMBER      = "1020416304";
    private static final String OTP_CODE          = "1111";

    @BeforeMethod
    public void openHomePage() {
        new HomePage().open();
        loginOtpPage = new LoginOtpPage().dismissBanner();
    }

    @Test(groups = {"smoke", "regression"},
            description = "Verify the login popup opens after clicking the header login button")
    public void loginPopupOpensSuccessfully() {
        loginOtpPage.clickLoginButton();

        assertThat(loginOtpPage.isLoginPopupOpen())
                .as("Login popup should be visible after clicking the login button")
                .isTrue();
    }

    @Test(groups = {"smoke", "regression"},
            description = "Verify entering phone number and clicking Send OTP shows the OTP fields")
    public void sendOtpShowsOtpInputFields() {
        loginOtpPage.clickLoginButton()
                .selectCountryCodeByText(COUNTRY_CODE_TEXT)
                .enterPhoneNumber(PHONE_NUMBER)
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
        loginOtpPage.clickLoginButton()
                .selectCountryCodeByText(COUNTRY_CODE_TEXT)
                .enterPhoneNumber(PHONE_NUMBER)
                .clickSendOtp();

        assertThat(loginOtpPage.isOtpStepVisible())
                .as("OTP fields should be visible before entering the code")
                .isTrue();

        loginOtpPage.enterOtp(OTP_CODE);

        assertThat(loginOtpPage.isVerifyButtonEnabled())
                .as("Verify button should be enabled after entering all OTP digits")
                .isTrue();

        loginOtpPage.clickVerify();

        log.info("OTP verification submitted. Manually confirm expected post-login state.");
    }

    @Test(groups = "regression",
            description = "Verify the login popup can be closed without completing the flow")
    public void loginPopupCanBeClosed() {
        loginOtpPage.clickLoginButton();

        assertThat(loginOtpPage.isLoginPopupOpen())
                .as("Popup should be open before attempting to close it")
                .isTrue();

        loginOtpPage.closePopup();

        assertThat(loginOtpPage.isLoginPopupOpen())
                .as("Popup should no longer be visible after closing it")
                .isFalse();
    }
}