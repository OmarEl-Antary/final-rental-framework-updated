package com.finalrental.tests;

import com.finalrental.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CancelOrderTest extends BaseTest {

    @BeforeMethod
    public void setUp() {
        new HomePage().open();
        LoginOtpPage loginOtpPage = new LoginOtpPage().dismissBanner();
        loginOtpPage.clickLoginButton()
                .selectCountryCodeByText("+20")
                .enterPhoneNumber(com.finalrental.data.TestContext.getRegisteredPhone())                .clickSendOtp()
                .enterOtp("1111")
                .clickVerify();
        log.info("✔ تم تسجيل الدخول بنجاح");
    }

    @Test(groups = {"smoke", "regression"},
            description = "إلغاء الطلب من صفحة تفاصيل الطلب")
    public void cancelOrderFromDetails() {
        EditOrderPage editOrderPage = new EditOrderPage();

        log.info("=== Step 1: الضغط على بيانات حسابي ===");
        editOrderPage.clickProfile();

        log.info("=== Step 2: الضغط على طلباتي ===");
        editOrderPage.clickMyOrders();

        log.info("=== Step 3: الضغط على خيارات الطلب ===");
        editOrderPage.clickOrderOptions();

        log.info("=== Step 4: الضغط على عرض الطلب ===");
        editOrderPage.clickViewOrder();

        log.info("=== Step 5: الضغط على إلغاء الطلب ===");
        editOrderPage.clickCancelOrder();

        log.info("=== Step 6: الضغط على تأكيد الإلغاء ===");
        editOrderPage.clickConfirmCancel();

        log.info("=== Step 7: كتابة سبب الإلغاء ===");
        editOrderPage.enterCancelReason("لم يعد لي الحاجه للطلب");

        log.info("=== Step 8: الضغط على زرار الإلغاء النهائي ===");
        editOrderPage.clickConfirmCancel();

        log.info("=== Step 9: التحقق من نجاح الإلغاء ===");
        assertThat(editOrderPage.isOrderCancelledSuccessfully())
                .as("يجب أن يتم إلغاء الطلب بنجاح")
                .isTrue();
        log.info("🎉 تم إلغاء الطلب بنجاح!");
    }
}