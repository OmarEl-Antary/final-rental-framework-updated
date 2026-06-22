package com.finalrental.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class LoginOtpPage extends BasePage {

    @FindBy(id = "custom-login1")
    private WebElement loginButton;

    @FindBy(id = "country-code-register")
    private WebElement countryCodeSelect;

    @FindBy(id = "phone-register")
    private WebElement phoneInput;

    @FindBy(id = "custom-checkphone")
    private WebElement sendOtpButton;

    @FindBy(css = "input.code-input")
    private List<WebElement> otpInputs;

    @FindBy(id = "custom-verify")
    private WebElement verifyButton;

    @FindBy(id = "custom-close2")
    private WebElement closePopupButton2;

    @FindBy(id = "custom-close3")
    private WebElement closePopupButton3;

    public LoginOtpPage dismissBanner() {
        try {
            executeScript("document.body.click();");
            log.info("Clicked body to dismiss any open banner.");
        } catch (Exception e) {
            log.debug("Could not dismiss banner: {}", e.getMessage());
        }
        return this;
    }

    private void closeAnyOpenModal() {
        try {
            executeScript(
                    "document.querySelectorAll('.modal').forEach(m => { " +
                            "  m.classList.remove('show'); " +
                            "  m.style.display = 'none'; " +
                            "});" +
                            "document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());" +
                            "document.body.classList.remove('modal-open');" +
                            "document.body.style.overflow = '';" +
                            "document.body.style.paddingRight = '';"
            );
            log.info("Closed any open modal/popup via JS.");
        } catch (Exception e) {
            log.debug("No modal to close or JS execution failed: {}", e.getMessage());
        }
    }

    public LoginOtpPage clickLoginButton() {
        log.info("Clicking header login button (#custom-login1)");
        closeAnyOpenModal();
        click(loginButton);
        return this;
    }

    public LoginOtpPage selectCountryCodeByValue(String value) {
        log.info("Selecting country code by value via JS: {}", value);
        waitForPresence(org.openqa.selenium.By.id("country-code-register"));
        executeScript(
                "var el = arguments[0];" +
                        "el.value = arguments[1];" +
                        "el.dispatchEvent(new Event('change', { bubbles: true }));",
                countryCodeSelect, value
        );
        return this;
    }

    public LoginOtpPage selectCountryCodeByText(String visibleText) {
        log.info("Selecting country code by visible text via JS: {}", visibleText);
        waitForPresence(org.openqa.selenium.By.id("country-code-register"));
        executeScript(
                "var el = arguments[0];" +
                        "var text = arguments[1];" +
                        "for (var i = 0; i < el.options.length; i++) {" +
                        "  if (el.options[i].text.trim() === text.trim() || el.options[i].text.includes(text)) {" +
                        "    el.value = el.options[i].value;" +
                        "    el.dispatchEvent(new Event('change', { bubbles: true }));" +
                        "    break;" +
                        "  }" +
                        "}",
                countryCodeSelect, visibleText
        );
        return this;
    }

    public LoginOtpPage enterPhoneNumber(String phoneNumber) {
        log.info("Entering phone number: {}", phoneNumber);
        type(phoneInput, phoneNumber);
        return this;
    }

    public LoginOtpPage clickSendOtp() {
        log.info("Clicking Send OTP button (#custom-checkphone)");
        click(sendOtpButton);
        return this;
    }

    public LoginOtpPage enterOtp(String otpCode) {
        log.info("Entering OTP code: {}", otpCode);

        if (otpCode.length() != otpInputs.size()) {
            log.warn("OTP code length ({}) does not match number of OTP input fields ({})",
                    otpCode.length(), otpInputs.size());
        }

        for (int i = 0; i < otpCode.length() && i < otpInputs.size(); i++) {
            WebElement digitInput = otpInputs.get(i);
            String digit = String.valueOf(otpCode.charAt(i));
            waitForVisible(digitInput);
            digitInput.clear();
            digitInput.sendKeys(digit);
        }
        return this;
    }

    public int getOtpInputCount() {
        return otpInputs.size();
    }

    public void clickVerify() {
        log.info("Clicking Verify button (#custom-verify)");
        click(verifyButton);
    }

    public void closePopup() {
        log.info("Closing login popup (#custom-close2)");
        try {
            click(closePopupButton2);
        } catch (Exception e) {
            log.debug("#custom-close2 not clickable, trying #custom-close3 instead.");
            click(closePopupButton3);
        }
    }

    public boolean isLoginPopupOpen() {
        return isElementVisible(phoneInput);
    }

    public boolean isOtpStepVisible() {
        return !otpInputs.isEmpty() && isElementVisible(otpInputs.get(0));
    }

    public boolean isSendOtpButtonEnabled() {
        return isElementEnabled(sendOtpButton);
    }

    public boolean isVerifyButtonEnabled() {
        return isElementEnabled(verifyButton);
    }
}