package com.finalrental.tests;

import com.finalrental.data.TestContext;
import com.finalrental.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterTest extends BaseTest {

    private static final String OTP_CODE     = "1111";
    private static final String COUNTRY_CODE = "+20";

    @BeforeMethod
    public void openHomePage() {
        new HomePage().open();
    }

    @Test(groups = {"smoke", "regression"},
            description = "تسجيل حساب جديد برقم مصري عشوائي")
    public void registerNewUser() {
        String phone     = RegisterPage.generateEgyptianPhone();
        String name      = RegisterPage.generateRandomName();
        String email     = RegisterPage.generateRandomEmail();
        String instagram = RegisterPage.generateRandomInstagram();

        // حفظ الرقم عشان اللوجين يستخدمه
        TestContext.setRegisteredPhone(phone);

        log.info("Phone: {}", phone);
        log.info("Name: {}", name);
        log.info("Email: {}", email);
        log.info("Instagram: {}", instagram);

        RegisterPage registerPage = new RegisterPage();

        log.info("=== Step 1: فتح الـ popup ===");
        registerPage.clickLoginRegisterButton();

        log.info("=== Step 2: اختيار كود الدولة ===");
        registerPage.selectCountryCode(COUNTRY_CODE);

        log.info("=== Step 3: إدخال رقم الهاتف ===");
        registerPage.enterPhone(phone);

        log.info("=== Step 4: إرسال الـ OTP ===");
        registerPage.clickSendOtp();

        log.info("=== Step 5: إدخال الـ OTP ===");
        registerPage.enterOtp(OTP_CODE);

        log.info("=== Step 6: التحقق ===");
        registerPage.clickVerify();

        assertThat(registerPage.isRegistrationFormVisible())
                .as("يجب أن يظهر فورم التسجيل")
                .isTrue();
        log.info("✔ فورم التسجيل ظهر");

        log.info("=== Step 7: ملء البيانات ===");
        registerPage.enterName(name)
                .enterEmail(email)
                .enterInstagram(instagram);

        log.info("=== Step 8: الموافقة على الشروط ===");
        registerPage.acceptTerms();

        log.info("=== Step 9: إكمال التسجيل ===");
        registerPage.clickRegister();

        assertThat(registerPage.isRegistrationSuccessful())
                .as("يجب أن ينجح التسجيل")
                .isTrue();
        log.info("🎉 تم التسجيل بنجاح! الرقم: {}", phone);
    }
}