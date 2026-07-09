package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CompleteOrderPage extends BasePage {

    @FindBy(id = "verify_via_input__")
    private WebElement identityInput;

    @FindBy(id = "custom-endOrder")
    private WebElement endOrderButton;

    public CompleteOrderPage enterIdentityNumber(String identityNumber) {
        log.info("Entering identity number: {}", identityNumber);
        waitForVisible(By.id("verify_via_input__"));
        identityInput.clear();
        identityInput.sendKeys(identityNumber);
        log.info("Identity number entered: {}", identityNumber);
        return this;
    }

    public OrderConfirmationPage clickSubmitOrder() {
        log.info("Clicking 'استكمال الطلب' (#custom-endOrder)");
        waitForClickable(endOrderButton);
        scrollIntoView(endOrderButton);
        jsClick(endOrderButton);
        // استنى popup الشروط يفتح
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("termsModal")));
        log.info("Terms modal appeared.");
        return new OrderConfirmationPage();
    }

    public boolean isCompleteOrderPageLoaded() {
        return isElementPresent(By.id("verify_via_input__"));
    }
}