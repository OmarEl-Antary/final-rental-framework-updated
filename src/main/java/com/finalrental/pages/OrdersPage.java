package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class OrdersPage extends BasePage {

    @FindBy(css = "button.menu-toggle")
    private WebElement firstOrderMenuToggle;

    public OrdersPage clickOrderMenu() {
        log.info("Clicking order 3-dots menu");
        waitForClickable(firstOrderMenuToggle);
        jsClick(firstOrderMenuToggle);
        log.info("Order menu opened.");
        return this;
    }

    public void clickViewOrderDetails() {
        log.info("Clicking 'عرض الطلب'");
        WebElement viewBtn = waitForClickable(
                By.xpath("//a[contains(., 'عرض الطلب')]"));
        jsClick(viewBtn);
        waitForPageLoad();
        log.info("Order details page opened. URL: {}", driver.getCurrentUrl());
    }

    public boolean isOrdersPageLoaded() {
        return isElementPresent(By.cssSelector("button.menu-toggle"));
    }
}