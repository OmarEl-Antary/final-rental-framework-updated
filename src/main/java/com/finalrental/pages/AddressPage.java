package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Random;

public class AddressPage extends BasePage {

    @FindBy(css = "a[href*='my-addresses']")
    private WebElement myAddressesLink;

    @FindBy(css = "a.add_new_address__")
    private WebElement addNewAddressButton;

    @FindBy(css = "input[name='name']")
    private WebElement addressNameInput;

    @FindBy(css = "button.dropdown-toggle")
    private WebElement cityDropdown;

    @FindBy(css = "textarea[name='description']")
    private WebElement addressDetailsInput;

    @FindBy(id = "searchInput")
    private WebElement mapSearchInput;

    @FindBy(id = "submit-address-button")
    private WebElement submitAddressButton;

    // ── توليد بيانات عشوائية ──────────────────────────────────────────────

    public static String generateAddressName() {
        String[] names = {
                "منزلي", "فيلا", "شقة", "المنزل", "العمل",
                "البيت", "الاستراحة", "الشاليه", "المكتب", "السكن"
        };
        String name = names[new Random().nextInt(names.length)] +
                "_" + System.currentTimeMillis() % 10000;
        org.apache.logging.log4j.LogManager
                .getLogger(AddressPage.class)
                .info("Generated address name: {}", name);
        return name;
    }

    public static String generateJeddahAddress() {
        String[] addresses = {
                "حي الروضة، شارع الأمير سلطان، جدة",
                "حي الزهراء، شارع التحلية، جدة",
                "حي الشاطئ، شارع الكورنيش، جدة",
                "حي السلامة، شارع الأمير ماجد، جدة",
                "حي الحمراء، شارع قريش، جدة",
                "حي النزهة، شارع الإمام الشافعي، جدة",
                "حي البوادي، شارع عبدالله السليمان، جدة"
        };
        String address = addresses[new Random().nextInt(addresses.length)];
        org.apache.logging.log4j.LogManager
                .getLogger(AddressPage.class)
                .info("Generated Jeddah address: {}", address);
        return address;
    }

    // ── Add Address Steps ─────────────────────────────────────────────────

    public AddressPage clickMyAddresses() {
        log.info("Clicking 'عناويني'");
        waitForClickable(myAddressesLink);
        jsClick(myAddressesLink);
        waitForPageLoad();
        log.info("Navigated to my addresses page.");
        return this;
    }

    public AddressPage clickAddNewAddress() {
        log.info("Clicking 'أضف عنوان'");
        waitForClickable(addNewAddressButton);
        jsClick(addNewAddressButton);
        waitForPageLoad();
        log.info("Navigated to add address page.");
        return this;
    }

    public AddressPage enterAddressName(String name) {
        log.info("Entering address name: {}", name);
        waitForVisible(By.cssSelector("input[name='name']"));
        addressNameInput.clear();
        addressNameInput.sendKeys(name);
        return this;
    }

    public AddressPage selectCity(String cityName) {
        log.info("Selecting city: {}", cityName);
        jsClick(cityDropdown);
        WebElement cityOption = waitForClickable(
                By.xpath("//a[@role='option']//span[@class='text' and contains(text(),'" + cityName + "')]"));
        jsClick(cityOption);
        log.info("City selected: {}", cityName);
        return this;
    }

    public AddressPage enterAddressDetails(String details) {
        log.info("Entering address details: {}", details);
        waitForVisible(By.cssSelector("textarea[name='description']"));
        addressDetailsInput.clear();
        addressDetailsInput.sendKeys(details);
        return this;
    }

    public AddressPage searchOnMap(String searchText) {
        log.info("Searching on map: {}", searchText);
        waitForVisible(By.id("searchInput"));
        mapSearchInput.clear();
        mapSearchInput.sendKeys(searchText);
        try { Thread.sleep(1500); } catch (Exception ignored) {}
        log.info("Map search entered.");
        return this;
    }

    public AddressPage clickSubmitAddress() {
        log.info("Clicking 'أضف العنوان' button");
        waitForClickable(submitAddressButton);
        scrollIntoView(submitAddressButton);
        jsClick(submitAddressButton);
        waitForPageLoad();
        log.info("Address submitted.");
        return this;
    }

    public boolean isAddressAddedSuccessfully() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("my-addresses"),
                    ExpectedConditions.urlContains("addresses")
            ));
            log.info("Address added successfully. ✅");
            return true;
        } catch (Exception e) {
            log.warn("Address not confirmed: {}", e.getMessage());
            return false;
        }
    }

    // ── Edit Address Steps ────────────────────────────────────────────────

    public AddressPage clickEditLastAddress() {
        log.info("Clicking edit button on last address");
        List<WebElement> editButtons = driver.findElements(
                By.cssSelector("a.edit_address"));
        WebElement lastEdit = editButtons.get(editButtons.size() - 1);
        jsClick(lastEdit);
        waitForPageLoad();
        log.info("Navigated to edit address page.");
        return this;
    }

    public AddressPage clickSubmitEditAddress() {
        log.info("Clicking 'تعديل العنوان' submit button");
        WebElement btn = waitForClickable(
                By.cssSelector("button.address_submit_button__"));
        scrollIntoView(btn);
        jsClick(btn);
        waitForPageLoad();
        log.info("Address updated.");
        return this;
    }

    public boolean isAddressUpdatedSuccessfully() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("my-addresses"),
                    ExpectedConditions.urlContains("addresses")
            ));
            log.info("Address updated successfully. ✅");
            return true;
        } catch (Exception e) {
            log.warn("Address update not confirmed: {}", e.getMessage());
            return false;
        }
    }
}