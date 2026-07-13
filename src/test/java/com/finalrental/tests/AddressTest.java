package com.finalrental.tests;

import com.finalrental.data.TestContext;
import com.finalrental.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressTest extends BaseTest {

    @BeforeMethod
    public void setUp() {
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
            description = "إضافة عنوان جديد في جدة")
    public void addNewAddress() {
        String addressName    = AddressPage.generateAddressName();
        String addressDetails = AddressPage.generateJeddahAddress();

        log.info("=== Step 1: الضغط على بيانات حسابي ===");
        new EditOrderPage().clickProfile();

        log.info("=== Step 2: الضغط على عناويني ===");
        AddressPage addressPage = new AddressPage();
        addressPage.clickMyAddresses();

        log.info("=== Step 3: الضغط على أضف عنوان ===");
        addressPage.clickAddNewAddress();

        log.info("=== Step 4: إدخال اسم العنوان ===");
        addressPage.enterAddressName(addressName);

        log.info("=== Step 5: اختيار مدينة جدة ===");
        addressPage.selectCity("فرع جدة");

        log.info("=== Step 6: إدخال تفاصيل العنوان ===");
        addressPage.enterAddressDetails(addressDetails);

        log.info("=== Step 7: البحث على الخريطة ===");
        addressPage.searchOnMap(addressDetails);

        log.info("=== Step 8: الضغط على أضف العنوان ===");
        addressPage.clickSubmitAddress();

        log.info("=== Step 9: التحقق من نجاح الإضافة ===");
        assertThat(addressPage.isAddressAddedSuccessfully())
                .as("يجب أن يتم إضافة العنوان بنجاح")
                .isTrue();
        log.info("تم إضافة العنوان بنجاح!");
    }

    @Test(groups = {"regression"},
            description = "تعديل آخر عنوان")
    public void editLastAddress() {
        String newName    = AddressPage.generateAddressName();
        String newDetails = AddressPage.generateJeddahAddress();

        log.info("=== Step 1: الضغط على بيانات حسابي ===");
        new EditOrderPage().clickProfile();

        log.info("=== Step 2: الضغط على عناويني ===");
        AddressPage addressPage = new AddressPage();
        addressPage.clickMyAddresses();

        log.info("=== Step 3: الضغط على تعديل آخر عنوان ===");
        addressPage.clickEditLastAddress();

        log.info("=== Step 4: تعديل اسم العنوان ===");
        addressPage.enterAddressName(newName);

        log.info("=== Step 5: تعديل تفاصيل العنوان ===");
        addressPage.enterAddressDetails(newDetails);

        log.info("=== Step 6: البحث على الخريطة ===");
        addressPage.searchOnMap(newDetails);

        log.info("=== Step 7: الضغط على تعديل العنوان ===");
        addressPage.clickSubmitEditAddress();

        log.info("=== Step 8: التحقق من نجاح التعديل ===");
        assertThat(addressPage.isAddressUpdatedSuccessfully())
                .as("يجب أن يتم تعديل العنوان بنجاح")
                .isTrue();
        log.info("تم تعديل العنوان بنجاح!");
    }
}