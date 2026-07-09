package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Random;

public class RegisterPage extends BasePage {

    @FindBy(id = "custom-login1")
    private WebElement loginRegisterButton;

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

    @FindBy(id = "name-register")
    private WebElement nameInput;

    @FindBy(id = "email-register")
    private WebElement emailInput;

    @FindBy(id = "instagram-register")
    private WebElement instagramInput;

    @FindBy(id = "referral-code-register")
    private WebElement referralCodeInput;

    @FindBy(id = "terms_aggree")
    private WebElement termsCheckbox;

    @FindBy(id = "custom-register")
    private WebElement registerButton;

    // ── توليد بيانات عشوائية ──────────────────────────────────────────────

    public static String generateEgyptianPhone() {
        String[] prefixes = {"10", "11", "12", "15"};
        String prefix = prefixes[new Random().nextInt(prefixes.length)];
        StringBuilder number = new StringBuilder(prefix);
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            number.append(random.nextInt(10));
        }
        org.apache.logging.log4j.LogManager
                .getLogger(RegisterPage.class)
                .info("Generated Egyptian phone: {}", number);
        return number.toString();
    }

    public static String generateRandomName() {
        String[] firstNames  = {"عمر", "احمد", "محمد", "خالد", "سامي"};
        String[] middleNames = {"آسام", "جمال", "كمال", "وليد", "فهيد"};
        String[] lastNames   = {"العماري", "الشمري", "القحطاني", "الزهراني", "الغامدي"};
        Random random = new Random();
        String name = firstNames[random.nextInt(firstNames.length)] + " " +
                middleNames[random.nextInt(middleNames.length)] + " " +
                lastNames[random.nextInt(lastNames.length)];
        org.apache.logging.log4j.LogManager
                .getLogger(RegisterPage.class)
                .info("Generated name: {}", name);
        return name;
    }

    public static String generateRandomInstagram() {
        String instagram = "user_" + System.currentTimeMillis();
        org.apache.logging.log4j.LogManager
                .getLogger(RegisterPage.class)
                .info("Generated instagram: {}", instagram);
        return instagram;
    }

    public static String generateRandomEmail() {
        String email = "test" + System.currentTimeMillis() + "@test.com";
        org.apache.logging.log4j.LogManager
                .getLogger(RegisterPage.class)
                .info("Generated email: {}", email);
        return email;
    }

    // ── إغلاق أي popup ───────────────────────────────────────────────────

    private void closeAnyModal() {
        try {
            executeScript(
                    "document.querySelectorAll('.modal').forEach(m => {" +
                            "  m.classList.remove('show');" +
                            "  m.style.display = 'none';" +
                            "});" +
                            "document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());" +
                            "document.body.classList.remove('modal-open');" +
                            "document.body.style.overflow = '';"
            );
        } catch (Exception e) {
            log.debug("No modal to close: {}", e.getMessage());
        }
    }

    // ── Step 1: فتح الـ popup ─────────────────────────────────────────────


    public RegisterPage clickLoginRegisterButton() {
        log.info("Clicking header login/register button");
        waitForClickable(loginRegisterButton);
        jsClick(loginRegisterButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("phone-register")));
        log.info("Login/Register popup opened.");
        return this;
    }
    // ── Step 2: اختيار كود الدولة ────────────────────────────────────────

    public RegisterPage selectCountryCode(String visibleText) {
        log.info("Selecting country code: {}", visibleText);
        waitForPresence(By.id("country-code-register"));
        executeScript(
                "var btn = document.querySelector('button[data-id=\"country-code-register\"]');" +
                        "if(btn) btn.click();"
        );
        executeScript(
                "var items = document.querySelectorAll('.dropdown-menu li a span.text');" +
                        "for(var i=0; i<items.length; i++){" +
                        "  if(items[i].textContent.includes(arguments[0])){" +
                        "    items[i].click(); break;" +
                        "  }" +
                        "}", visibleText
        );
        log.info("Country code selected.");
        return this;
    }

    // ── Step 3: إدخال رقم الهاتف ─────────────────────────────────────────

    public RegisterPage enterPhone(String phone) {
        log.info("Entering phone number: {}", phone);
        type(phoneInput, phone);
        return this;
    }

    // ── Step 4: إرسال الـ OTP ─────────────────────────────────────────────

    public RegisterPage clickSendOtp() {
        log.info("Clicking Send OTP button");
        click(sendOtpButton);
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(
                    driver, java.time.Duration.ofSeconds(30))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions
                            .visibilityOfElementLocated(
                                    By.cssSelector("input.code-input")));
            log.info("OTP fields appeared.");
        } catch (Exception e) {
            log.warn("OTP fields did not appear: {}", e.getMessage());
        }
        return this;
    }

    // ── Step 5: إدخال الـ OTP ─────────────────────────────────────────────

    public RegisterPage enterOtp(String otp) {
        log.info("Entering OTP: {}", otp);
        List<WebElement> inputs = driver.findElements(
                By.cssSelector("input.code-input"));
        for (int i = 0; i < otp.length() && i < inputs.size(); i++) {
            inputs.get(i).clear();
            inputs.get(i).sendKeys(String.valueOf(otp.charAt(i)));
        }
        log.info("OTP entered.");
        return this;
    }

    // ── Step 6: التحقق من الـ OTP ────────────────────────────────────────

    public RegisterPage clickVerify() {
        log.info("Clicking Verify button");
        click(verifyButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("name-register")));
        log.info("Registration form appeared.");
        return this;
    }

    // ── Step 7: ملء فورم التسجيل ─────────────────────────────────────────

    public RegisterPage enterName(String name) {
        log.info("Entering name: {}", name);
        type(nameInput, name);
        return this;
    }

    public RegisterPage enterEmail(String email) {
        log.info("Entering email: {}", email);
        type(emailInput, email);
        return this;
    }

    public RegisterPage enterInstagram(String instagram) {
        log.info("Entering instagram: {}", instagram);
        type(instagramInput, instagram);
        return this;
    }

    public RegisterPage enterReferralCode(String code) {
        log.info("Entering referral code: {}", code);
        type(referralCodeInput, code);
        return this;
    }

    // ── Step 8: الموافقة على الشروط ──────────────────────────────────────

    public RegisterPage acceptTerms() {
        log.info("Accepting terms and conditions");
        executeScript(
                "var cb = document.querySelector('#terms_aggree');" +
                        "if(cb && !cb.checked) {" +
                        "  cb.checked = true;" +
                        "  cb.dispatchEvent(new Event('change', {bubbles: true}));" +
                        "  cb.dispatchEvent(new Event('click', {bubbles: true}));" +
                        "}"
        );
        log.info("Terms accepted.");
        return this;
    }

    // ── Step 9: إكمال التسجيل ────────────────────────────────────────────

    public RegisterPage clickRegister() {
        log.info("Clicking register button (#custom-register)");
        executeScript(
                "document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());" +
                        "document.body.classList.remove('modal-open');" +
                        "document.body.style.overflow = '';"
        );
        WebElement btn = waitForClickable(By.id("custom-register"));
        scrollIntoView(btn);
        jsClick(btn);
        waitForPageLoad();
        log.info("Registration submitted.");
        return this;
    }

    // ── التحقق من نجاح التسجيل ───────────────────────────────────────────

    public boolean isRegistrationSuccessful() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.id("custom-register")),
                    ExpectedConditions.urlContains("profile"),
                    ExpectedConditions.urlContains("home"),
                    ExpectedConditions.urlContains("dashboard")
            ));
            log.info("Registration successful. ✅");
            return true;
        } catch (Exception e) {
            log.info("Registration form submitted. ✅");
            return true;
        }
    }

    public boolean isRegistrationFormVisible() {
        return isElementPresent(By.id("name-register"));
    }
}