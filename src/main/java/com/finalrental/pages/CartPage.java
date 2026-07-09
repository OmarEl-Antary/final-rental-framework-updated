package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends BasePage {

    @FindBy(id = "from_date")
    private WebElement fromDateInput;

    @FindBy(id = "to_date")
    private WebElement toDateInput;

    public CartPage selectFromDate(String date) {
        log.info("Selecting pickup date: {}", date);
        executeScript(
                "document.querySelector('#from_date')._flatpickr.setDate(arguments[0], true);",
                date
        );
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#from-time option:not([hidden])")));
        log.info("From-time options loaded.");
        return this;
    }

    public CartPage selectFromTime(String timeValue) {
        log.info("Selecting pickup time: {}", timeValue);
        executeScript(
                "document.querySelector('button[data-id=\"from-time\"]').click();"
        );
        WebElement option = waitForVisible(
                By.xpath(
                        "//div[contains(@class,'from-time')]" +
                                "//span[@class='text' and contains(text(),'" + timeValue + "')]"));
        jsClick(option);
        executeScript("document.body.click();");
        log.info("Pickup time selected.");
        return this;
    }

    public CartPage selectToDate(String date) {
        log.info("Selecting return date: {}", date);
        executeScript(
                "document.querySelector('#to_date')._flatpickr.setDate(arguments[0], true);",
                date
        );
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#to-time option:not([hidden])")));
        log.info("To-time options loaded.");
        return this;
    }

    public CartPage selectToTime(String timeValue) {
        log.info("Selecting return time: {}", timeValue);
        executeScript(
                "document.querySelector('button[data-id=\"to-time\"]').click();"
        );
        WebElement option = waitForVisible(
                By.xpath(
                        "//div[contains(@class,'to-time')]" +
                                "//span[@class='text' and contains(text(),'" + timeValue + "')]"));
        jsClick(option);
        executeScript("document.body.click();");
        log.info("Return time selected.");
        return this;
    }

    public CompleteOrderPage clickSubmitCart() {
        log.info("Submitting cart via submitCartDates()");
        executeScript("submitCartDates();");
        try {
            wait.until(ExpectedConditions.not(
                    ExpectedConditions.urlToBe("https://testing.final.sa/cart")));
            log.info("Navigated to: {}", driver.getCurrentUrl());
        } catch (Exception e) {
            log.warn("URL did not change: {}", e.getMessage());
        }
        return new CompleteOrderPage();
    }

    public boolean isCartPageLoaded() {
        return isElementPresent(By.id("from_date"));
    }

    public void clearCart() {
        navigateTo("https://testing.final.sa/cart");
        executeScript(
                "document.querySelectorAll('button.delete, .cart-item-delete, " +
                        "[class*=\"delete\"]').forEach(btn => btn.click());"
        );
        log.info("Cart cleared.");
    }
}