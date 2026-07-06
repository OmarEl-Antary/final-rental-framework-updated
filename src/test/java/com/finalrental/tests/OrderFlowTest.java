package com.finalrental.tests;

import com.finalrental.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderFlowTest extends BaseTest {

    private static final String CITY_VALUE = "6";
    private static final String FROM_DATE;
    private static final String TO_DATE;
    private static final String FROM_TIME = "7:00";
    private static final String TO_TIME   = "7:00";

    static {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        FROM_DATE = LocalDate.now().plusDays(2).format(fmt);
        TO_DATE   = LocalDate.now().plusDays(5).format(fmt);
    }

    @BeforeMethod
    public void openHomePage() {
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
            description = "رحلة الطلب الكاملة")
    public void fullOrderFlow() {

        // مسح السلة أول حاجة
        log.info("Clearing cart...");
        new CartPage().clearCart();

        log.info("=== Step 1: اختيار مدينة جدة والبحث ===");
        ProductsPage productsPage = new HomePage()
                .selectCity(CITY_VALUE)
                .clickSearch();
        assertThat(productsPage.hasProducts())
                .as("يجب أن تظهر منتجات بعد البحث")
                .isTrue();
        log.info("✔ صفحة المنتجات ظهرت بنجاح");

        log.info("=== Step 2: إضافة منتج للسلة ===");
        productsPage.clickAddToCart();
        log.info("✔ Bottom Sheet ظهر");

        log.info("=== Step 3: الانتقال لصفحة السلة ===");
        CartPage cartPage = productsPage.clickContinueToOrder();
        assertThat(cartPage.isCartPageLoaded())
                .as("يجب أن تظهر صفحة السلة")
                .isTrue();
        log.info("✔ صفحة السلة ظهرت");

        log.info("=== Step 4: اختيار التواريخ والأوقات ===");
        cartPage.selectFromDate(FROM_DATE)
                .selectFromTime(FROM_TIME)
                .selectToDate(TO_DATE)
                .selectToTime(TO_TIME);
        log.info("✔ تم اختيار التواريخ والأوقات");

        log.info("=== Step 5: استكمال الطلب من السلة ===");
        OrderConfirmationPage confirmPage = cartPage.clickSubmitCart();
        log.info("✔ تم الضغط على استكمال الطلب");

        log.info("=== Step 6: الضغط على استكمال الطلب ===");
        confirmPage.clickEndOrder();
        log.info("✔ popup الشروط ظهر");

        log.info("=== Step 7: الموافقة على الشروط ===");
        confirmPage.clickAgreeToTerms();
        assertThat(confirmPage.isOrderCompletedModalVisible())
                .as("يجب أن يظهر popup تأكيد الطلب")
                .isTrue();
        log.info("✔ popup تأكيد الطلب ظهر");

        log.info("=== Step 8: متابعة الطلب ===");
        confirmPage.clickTrackOrder();
        assertThat(confirmPage.getCurrentUrl())
                .as("يجب الانتقال لصفحة الطلبات")
                .contains("orders");
        log.info("✔ تم الانتقال لصفحة الطلبات!");

        log.info("=== Step 9: إغلاق الـ popup ===");
        confirmPage.closeAnyPopup();
        log.info("✔ تم إغلاق الـ popup");

        log.info("=== Step 10: فتح تفاصيل الطلب ===");
        OrdersPage ordersPage = new OrdersPage();

// استنى لحد ما الطلبات تتحمل
        try { Thread.sleep(3000); } catch (Exception e) {}

        assertThat(ordersPage.isOrdersPageLoaded())
                .as("يجب أن تظهر قائمة الطلبات")
                .isTrue();
        ordersPage.clickOrderMenu();
        ordersPage.clickViewOrderDetails();
        assertThat(confirmPage.getCurrentUrl())
                .as("يجب أن تفتح صفحة تفاصيل الطلب")
                .contains("orders");
        log.info("✔ تفاصيل الطلب ظهرت بنجاح!");
        log.info("🎉 رحلة الطلب الكاملة اكتملت بنجاح!");

        log.info("=== Step 11: التحقق من صحة الحسابات ===");
        OrderSummaryPage summaryPage = new OrderSummaryPage();

        BigDecimal rental   = summaryPage.getRentalPrice();
        BigDecimal delivery = summaryPage.getDeliveryPrice();
        BigDecimal discount = summaryPage.getDiscountPrice();
        BigDecimal tax      = summaryPage.getTaxPrice();
        BigDecimal total    = summaryPage.getTotalPrice();

        BigDecimal expectedTax = rental.add(delivery)
                .subtract(discount)
                .multiply(new java.math.BigDecimal("0.15"))
                .setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal expectedTotal = rental.add(delivery)
                .subtract(discount)
                .add(tax)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        assertThat(tax.setScale(2, java.math.RoundingMode.HALF_UP))
                .as("الضريبة المتوقعة: %s | الضريبة الفعلية: %s",
                        expectedTax, tax)
                .isEqualByComparingTo(expectedTax);
        log.info("✔ حساب الضريبة صحيح: {}", tax);

        assertThat(total.setScale(2, java.math.RoundingMode.HALF_UP))
                .as("الإجمالي المتوقع: %s | الإجمالي الفعلي: %s",
                        expectedTotal, total)
                .isEqualByComparingTo(expectedTotal);
        log.info("✔ المبلغ الكلي صحيح: {}", total);
        log.info("🎉 كل الحسابات صحيحة!");
    }

}