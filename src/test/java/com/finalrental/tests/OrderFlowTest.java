package com.finalrental.tests;

import com.finalrental.data.TestContext;
import com.finalrental.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderFlowTest extends BaseTest {

    private static final String CITY_VALUE = "6";
    private static final String FROM_DATE;
    private static final String TO_DATE;
    private static final String FROM_TIME = "7:00";
    private static final String TO_TIME   = "7:00";

    static {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        FROM_DATE = LocalDate.now().plusDays(3).format(fmt);
        TO_DATE   = LocalDate.now().plusDays(7).format(fmt);
    }

    @BeforeMethod
    public void openHomePage() {
        new HomePage().open();
        LoginOtpPage loginOtpPage = new LoginOtpPage().dismissBanner();
        loginOtpPage.clickLoginButton()
                .selectCountryCodeByText("+20")
                .enterPhoneNumber(TestContext.getRegisteredPhone())
                .clickSendOtp()
                .enterOtp("1111")
                .clickVerify();
        log.info("✔ تم تسجيل الدخول بنجاح");
    }

    @Test(groups = {"smoke", "regression"},
            description = "رحلة الطلب الكاملة")
    public void fullOrderFlow() {

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
        CompleteOrderPage completePage = cartPage.clickSubmitCart();
        log.info("✔ تم الانتقال لصفحة إكمال الطلب");

        log.info("=== Step 6: إدخال رقم الهوية ===");
        completePage.enterIdentityNumber(TestContext.generateIdentityNumber());
        log.info("✔ تم إدخال رقم الهوية");

        log.info("=== Step 7: الضغط على استكمال الطلب ===");
        OrderConfirmationPage confirmPage = completePage.clickSubmitOrder();
        log.info("✔ popup الشروط ظهر");

        log.info("=== Step 8: الموافقة على الشروط ===");
        confirmPage.clickAgreeToTerms();
        assertThat(confirmPage.isOrderCompletedModalVisible())
                .as("يجب أن يظهر popup تأكيد الطلب")
                .isTrue();
        log.info("✔ popup تأكيد الطلب ظهر");

        log.info("=== Step 9: متابعة الطلب ===");
        confirmPage.clickTrackOrder();
        assertThat(confirmPage.getCurrentUrl())
                .as("يجب الانتقال لصفحة الطلبات")
                .contains("orders");
        log.info("✔ تم الانتقال لصفحة الطلبات!");

        log.info("=== Step 10: إغلاق الـ popup ===");
        confirmPage.closeAnyPopup();

        log.info("=== Step 11: فتح تفاصيل الطلب ===");
        OrdersPage ordersPage = new OrdersPage();
        ordersPage.clickOrderMenu();
        ordersPage.clickViewOrderDetails();

        log.info("=== Step 12: التحقق من الحسابات ===");
        OrderSummaryPage summaryPage = new OrderSummaryPage();
        BigDecimal rental   = summaryPage.getRentalPrice();
        BigDecimal delivery = summaryPage.getDeliveryPrice();
        BigDecimal discount = summaryPage.getDiscountPrice();
        BigDecimal tax      = summaryPage.getTaxPrice();
        BigDecimal total    = summaryPage.getTotalPrice();

        BigDecimal expectedTax = rental.add(delivery)
                .subtract(discount)
                .multiply(new BigDecimal("0.15"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedTotal = rental.add(delivery)
                .subtract(discount)
                .add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        assertThat(tax.setScale(2, RoundingMode.HALF_UP))
                .as("Expected Tax: %s | Actual Tax: %s", expectedTax, tax)
                .isEqualByComparingTo(expectedTax);
        log.info("Tax calculation correct: {}", tax);

        assertThat(total.setScale(2, RoundingMode.HALF_UP))
                .as("Expected Total: %s | Actual Total: %s", expectedTotal, total)
                .isEqualByComparingTo(expectedTotal);
        log.info("Total calculation correct: {}", total);
        log.info("Order flow completed successfully!");
    }
}