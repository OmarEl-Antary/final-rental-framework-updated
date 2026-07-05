package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
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

    @FindBy(id = "custom-resend")
    private WebElement resendButton;

    @FindBy(id = "custom-close2")
    private WebElement closePopupButton2;

    @FindBy(id = "custom-close3")
    private WebElement closePopupButton3;

    // ── Dismiss banner ────────────────────────────────────────────────────────

    public LoginOtpPage dismissBanner() {
        try {
            executeScript("document.body.click();");
            log.info("Clicked body to dismiss any open banner.");
        } catch (Exception e) {
            log.debug("Could not dismiss banner: {}", e.getMessage());
        }
        return this;
    }

    // ── Close any blocking modal ──────────────────────────────────────────────

    private void closeAnyOpenModal() {
        try {
            executeScript(
                    "document.querySelectorAll('.modal').forEach(m => {" +
                            "  m.classList.remove('show');" +
                            "  m.style.display = 'none';" +
                            "});" +
                            "document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());" +
                            "document.body.classList.remove('modal-open');" +
                            "document.body.style.overflow = '';" +
                            "document.body.style.paddingRight = '';"
            );
            log.info("Closed any open modal via JS.");
        } catch (Exception e) {
            log.debug("No modal to close: {}", e.getMessage());
        }
    }

    // ── Step 1: Click login button ────────────────────────────────────────────

    public LoginOtpPage clickLoginButton() {
        log.info("Clicking header login button (#custom-login1)");
        closeAnyOpenModal();
        click(loginButton);
        return this;
    }

    // ── Step 2: Select country code ───────────────────────────────────────────

    public LoginOtpPage selectCountryCodeByText(String visibleText) {
        log.info("Selecting country code: {}", visibleText);
        try {
            // فتح الـ Bootstrap selectpicker dropdown
            executeScript(
                    "var btn = document.querySelector(" +
                            "'button[data-id=\"country-code-register\"]');" +
                            "if(btn) btn.click();"
            );
            // اختيار الـ option المطلوب
            executeScript(
                    "var items = document.querySelectorAll(" +
                            "'.dropdown-menu li a span.text');" +
                            "for(var i=0; i<items.length; i++){" +
                            "  if(items[i].textContent.includes(arguments[0])){" +
                            "    items[i].click(); break;" +
                            "  }" +
                            "}", visibleText
            );
            log.info("Country code selected.");
        } catch (Exception e) {
            log.warn("Could not select country code: {}", e.getMessage());
        }
        return this;
    }

    // ── Step 3: Enter phone number ────────────────────────────────────────────

    public LoginOtpPage enterPhoneNumber(String phoneNumber) {
        log.info("Entering phone number: {}", phoneNumber);
        type(phoneInput, phoneNumber);
        return this;
    }

    // ── Step 4: Click Send OTP ────────────────────────────────────────────────

    public LoginOtpPage clickSendOtp() {
        log.info("Clicking Send OTP button (#custom-checkphone)");
        click(sendOtpButton);

        // استنى لحد ما الـ OTP popup يفتح (max 20 ثانية)
        log.info("Waiting for OTP popup to appear...");
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(
                    driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".code_shapes__, input.code-input")));
            log.info("OTP popup appeared successfully.");
        } catch (Exception e) {
            log.warn("OTP popup did not appear: {}", e.getMessage());
        }
        return this;
    }

    // ── Step 5: Enter OTP ─────────────────────────────────────────────────────

    public LoginOtpPage enterOtp(String otpCode) {
        log.info("Entering OTP: {}", otpCode);

        // استنى لحد ما الخانات تبقى clickable
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("input.code-input")));

        List<WebElement> inputs = driver.findElements(
                By.cssSelector("input.code-input"));

        for (int i = 0; i < otpCode.length() && i < inputs.size(); i++) {
            inputs.get(i).clear();
            inputs.get(i).sendKeys(String.valueOf(otpCode.charAt(i)));
        }
        log.info("OTP entered successfully.");
        return this;
    }

    // ── Step 6: Verify ────────────────────────────────────────────────────────

    public void clickVerify() {
        log.info("Clicking Verify button (#custom-verify)");
        click(verifyButton);
    }

    // ── Close popup ───────────────────────────────────────────────────────────

    public void closePopup() {
        log.info("Closing popup");
        try {
            click(closePopupButton2);
        } catch (Exception e) {
            click(closePopupButton3);
        }
    }

    // ── State checks ──────────────────────────────────────────────────────────

    public boolean isLoginPopupOpen() {
        return isElementPresent(By.id("phone-register")) &&
                isElementVisible(phoneInput);
    }

    public boolean isOtpStepVisible() {
        try {
            List<WebElement> inputs = driver.findElements(
                    By.cssSelector("input.code-input"));
            return !inputs.isEmpty() && inputs.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isVerifyButtonEnabled() {
        return isElementEnabled(verifyButton);
    }

    public int getOtpInputCount() {
        return driver.findElements(By.cssSelector("input.code-input")).size();
    }
}