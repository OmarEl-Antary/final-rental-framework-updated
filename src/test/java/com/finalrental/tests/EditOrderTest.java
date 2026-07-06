package com.finalrental.tests;

import com.finalrental.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EditOrderTest extends BaseTest {

    @BeforeMethod
    public void setUp() {
        new HomePage().open();
        LoginOtpPage loginOtpPage = new LoginOtpPage().dismissBanner();
        loginOtpPage.clickLoginButton()
                .selectCountryCodeByText("+20")
                .enterPhoneNumber("1020416304")
                .clickSendOtp()
                .enterOtp("1111")
                .clickVerify();
        log.info("✔ تم تسجيل الدخول بنجاح");
    }

    @Test(groups = {"smoke", "regression"},
            description = "تعديل الطلب من خيارات الطلب (3 نقاط)")
    public void editOrderFromMenu() {
        EditOrderPage editOrderPage = new EditOrderPage();

        log.info("=== Step 1: الضغط على بيانات حسابي ===");
        editOrderPage.clickProfile();

        log.info("=== Step 2: الضغط على طلباتي ===");
        editOrderPage.clickMyOrders();

        log.info("=== Step 3: الضغط على خيارات الطلب (3 نقاط) ===");
        editOrderPage.clickOrderOptions();

        log.info("=== Step 4: اختيار تعديل الطلب ===");
        editOrderPage.clickEditFromMenu();

        log.info("=== Step 5: الضغط على إضافة منتج ===");
        editOrderPage.clickAddProduct();

        log.info("=== Step 6: اختيار أول منتج ===");
        editOrderPage.clickFirstProduct();

        log.info("=== Step 7: إضافة المنتج للطلب ===");
        editOrderPage.clickAddToOrder();

        log.info("=== Step 8: إكمال الطلب الأول ===");
        editOrderPage.clickCompleteOrder();

        log.info("=== Step 9: إكمال الطلب الثاني ===");
        editOrderPage.clickCompleteOrderAddress();

        log.info("=== Step 10: التحقق من نجاح التعديل ===");
        assertThat(editOrderPage.isEditOrderButtonVisible())
                .as("زرار تعديل الطلب يجب أن يظهر بعد نجاح التعديل")
                .isTrue();
        log.info("🎉 تم تعديل الطلب بنجاح!");
    }
}