package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    @FindBy(id = "city_select")
    private WebElement citySelect;

    @FindBy(id = "dashboard_2")
    private WebElement searchButton;

    public HomePage open() {
        log.info("Opening Final Rental home page: {}", config.getBaseUrl());
        navigateTo(config.getBaseUrl());
        log.info("Home page loaded successfully.");
        return this;
    }

    public HomePage selectCity(String value) {
        log.info("Selecting city with value: {}", value);
        executeScript(
                "var el = document.querySelector('#city_select');" +
                        "el.value = arguments[0];" +
                        "el.dispatchEvent(new Event('change', { bubbles: true }));",
                value
        );
        return this;
    }

    public ProductsPage clickSearch() {
        log.info("Clicking search button (#dashboard_2)");
        try {
            WebElement btn = driver.findElement(By.id("dashboard_2"));
            btn.click();
        } catch (Exception e) {
            executeScript(
                    "document.querySelector('#dashboard_2').click();"
            );
        }
        waitForPageLoad();
        return new ProductsPage();
    }

    public boolean isLogoVisible() {
        return isElementPresent(
                By.cssSelector("nav .logo, .brand-logo, header .logo"));
    }
}