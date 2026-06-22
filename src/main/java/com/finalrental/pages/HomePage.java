package com.finalrental.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

    @FindBy(css = "nav .logo, .brand-logo, header .logo")
    private WebElement logo;

    public HomePage open() {
        log.info("Opening Final Rental home page: {}", config.getBaseUrl());
        navigateTo(config.getBaseUrl());
        log.info("Home page loaded successfully.");
        return this;
    }

    public boolean isLogoVisible() {
        return isElementVisible(logo);
    }
}