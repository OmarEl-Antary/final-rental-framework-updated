package com.finalrental.bdd.stepdefs;

import com.finalrental.pages.*;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class AddressSteps {

    private static final Logger log =
            LoggerFactory.getLogger(AddressSteps.class);

    private AddressPage addressPage;
    private String addressDetails;

    @And("the user clicks on my addresses")
    public void the_user_clicks_on_my_addresses() {
        addressPage = new AddressPage();
        addressPage.clickMyAddresses();
        log.info("✔ تم الضغط على عناويني");
    }

    @And("the user clicks add new address")
    public void the_user_clicks_add_new_address() {
        addressPage.clickAddNewAddress();
        log.info("✔ تم الضغط على أضف عنوان");
    }

    @And("the user enters a random address name")
    public void the_user_enters_a_random_address_name() {
        String name = AddressPage.generateAddressName();
        addressPage.enterAddressName(name);
        log.info("✔ تم إدخال اسم العنوان: {}", name);
    }

    @And("the user selects city {string}")
    public void the_user_selects_city(String city) {
        addressPage.selectCity(city);
        log.info("✔ تم اختيار المدينة: {}", city);
    }

    @And("the user enters Jeddah address details")
    public void the_user_enters_jeddah_address_details() {
        addressDetails = AddressPage.generateJeddahAddress();
        addressPage.enterAddressDetails(addressDetails);
        log.info("✔ تم إدخال تفاصيل العنوان: {}", addressDetails);
    }

    @And("the user searches on map with address details")
    public void the_user_searches_on_map_with_address_details() {
        addressPage.searchOnMap(addressDetails);
        log.info("✔ تم البحث على الخريطة");
    }

    @And("the user clicks submit address")
    public void the_user_clicks_submit_address() {
        addressPage.clickSubmitAddress();
        log.info("✔ تم الضغط على أضف العنوان");
    }

    @Then("the address should be added successfully")
    public void the_address_should_be_added_successfully() {
        Assert.assertTrue(addressPage.isAddressAddedSuccessfully(),
                "Address should be added successfully");
        log.info("🎉 تم إضافة العنوان بنجاح!");
    }

    @And("the user clicks edit last address")
    public void the_user_clicks_edit_last_address() {
        addressPage = new AddressPage();
        addressPage.clickEditLastAddress();
        log.info("✔ تم الضغط على تعديل العنوان");
    }

    @And("the user clicks submit edit address")
    public void the_user_clicks_submit_edit_address() {
        addressPage.clickSubmitEditAddress();
        log.info("✔ تم الضغط على تعديل العنوان");
    }

    @Then("the address should be updated successfully")
    public void the_address_should_be_updated_successfully() {
        Assert.assertTrue(addressPage.isAddressUpdatedSuccessfully(),
                "Address should be updated successfully");
        log.info("🎉 تم تعديل العنوان بنجاح!");
    }
}