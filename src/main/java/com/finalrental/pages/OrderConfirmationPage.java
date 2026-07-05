package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class OrderConfirmationPage extends BasePage {

    @FindBy(id = "custom-endOrder")
    private WebElement endOrderButton;

    @FindBy(css = "#termsModal .submitButton")
    private WebElement agreeButton;

    @FindBy(css = "#completOrderModal .blank_pages_link__")
    private WebElement trackOrderButton;

    @FindBy(css = ".close_modal_button")
    private WebElement closeModalButton;

    public OrderConfirmationPage clickEndOrder() {
        log.info("Clicking End Order button (#custom-endOrder)");
        scrollIntoView(endOrderButton);
        jsClick(endOrderButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("termsModal")));
        log.info("Terms modal appeared.");
        return this;
    }

    public OrderConfirmationPage clickAgreeToTerms() {
        log.info("Clicking 'Agree to Terms' button");
        waitForClickable(agreeButton);
        jsClick(agreeButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("completOrderModal")));
        log.info("Order confirmed modal appeared.");
        return this;
    }

    public void clickTrackOrder() {
        log.info("Clicking 'Track Order' button");
        waitForClickable(trackOrderButton);
        jsClick(trackOrderButton);
        waitForPageLoad();
        log.info("Navigated to orders list page.");
    }

    public void closeAnyPopup() {
        log.info("Closing any open popup");
        try {
            waitForVisible(By.cssSelector(".close_modal_button"));
            jsClick(closeModalButton);
            log.info("Popup closed.");
        } catch (Exception e) {
            log.debug("No popup to close: {}", e.getMessage());
            executeScript("document.body.click();");
        }
    }

    public boolean isConfirmationPageLoaded() {
        return isElementPresent(By.id("custom-endOrder"));
    }

    public boolean isTermsModalVisible() {
        return isElementPresent(By.cssSelector("#termsModal.show"));
    }

    public boolean isOrderCompletedModalVisible() {
        return isElementPresent(By.cssSelector("#completOrderModal.show"));
    }
}