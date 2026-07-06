package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class OrdersPage extends BasePage {

    @FindBy(css = "button.menu-toggle")
    private WebElement firstOrderMenuToggle;

    public OrdersPage navigateToOrdersList() {
        navigateTo("https://testing.final.sa/orders-list");
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("button.menu-toggle")));
        log.info("Navigated to orders list.");
        return this;
    }

    public OrdersPage clickOrderMenu() {
        log.info("Clicking order 3-dots menu");
        waitForVisible(By.cssSelector("button.menu-toggle"));
        WebElement btn = driver.findElement(
                By.cssSelector("button.menu-toggle"));
        jsClick(btn);
        log.info("Order menu opened.");
        return this;
    }

    public void clickViewOrderDetails() {
        log.info("Clicking 'عرض الطلب'");
        WebElement viewBtn = waitForClickable(
                By.xpath("//a[contains(., 'عرض الطلب')]"));
        jsClick(viewBtn);
        waitForPageLoad();
        log.info("Order details page opened. URL: {}",
                driver.getCurrentUrl());
    }

    public boolean isOrdersPageLoaded() {
        return driver.getCurrentUrl().contains("orders");
    }
}