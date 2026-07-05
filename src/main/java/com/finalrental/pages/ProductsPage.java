package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductsPage extends BasePage {

    @FindBy(id = "custom-addToCart")
    private WebElement addToCartButton;

    @FindBy(css = ".plus_button__")
    private WebElement plusButton;

    @FindBy(css = "span.count__")
    private WebElement quantityCount;

    @FindBy(css = "a.blank_pages_link__")
    private WebElement continueToOrderButton;

    public ProductsPage clickAddToCart() {
        log.info("Clicking Add to Cart button (#custom-addToCart)");
        waitForClickable(addToCartButton);
        scrollIntoView(addToCartButton);
        jsClick(addToCartButton);
        // استنى الـ Side Cart يفتح
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#sideCartOffcanvas.is-open, .oc-end.is-open")));
        log.info("Side cart opened.");
        return this;
    }

    public ProductsPage increaseQuantity(int times) {
        log.info("Increasing quantity {} time(s)", times);
        for (int i = 0; i < times; i++) {
            WebElement plus = waitForClickable(
                    By.cssSelector(".plus_button__"));
            jsClick(plus);
        }
        log.info("Quantity increased.");
        return this;
    }

    public String getQuantity() {
        return getText(quantityCount);
    }

    public CartPage clickContinueToOrder() {
        log.info("Navigating directly to cart");
        navigateTo("https://testing.final.sa/cart");
        return new CartPage();
    }

    public boolean hasProducts() {
        return isElementPresent(By.cssSelector(".product_card__"));
    }
}