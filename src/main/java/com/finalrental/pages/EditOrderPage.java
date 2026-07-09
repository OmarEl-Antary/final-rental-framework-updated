package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class EditOrderPage extends BasePage {

    @FindBy(css = "a.header_user_link__")
    private WebElement profileLink;

    @FindBy(css = "a[href*='orders-list']")
    private WebElement myOrdersLink;

    @FindBy(css = "a[href*='/alternative']")
    private WebElement addProductButton;

    @FindBy(css = ".product_add_to_cart")
    private WebElement addToOrderButton;

    @FindBy(id = "completeOrderAndAgree")
    private WebElement completeOrderButton;

    @FindBy(id = "editOrder")
    private WebElement editOrderButton;

    // ── إغلاق أي popup ───────────────────────────────────────────────────

    private void closeAnyModal() {
        try {
            executeScript(
                    "document.querySelectorAll('.modal').forEach(m => {" +
                            "  m.classList.remove('show');" +
                            "  m.style.display = 'none';" +
                            "});" +
                            "document.querySelectorAll('.modal-backdrop').forEach(b => b.remove());" +
                            "document.body.classList.remove('modal-open');" +
                            "document.body.style.overflow = '';"
            );
        } catch (Exception e) {
            log.debug("No modal to close: {}", e.getMessage());
        }
    }

    // ── Step 1: بيانات حسابي ─────────────────────────────────────────────

    public EditOrderPage clickProfile() {
        log.info("Clicking 'بيانات حسابي' in header");
        closeAnyModal();
        waitForClickable(profileLink);
        jsClick(profileLink);
        waitForPageLoad();
        log.info("Navigated to profile page.");
        return this;
    }

    // ── Step 2: طلباتي ───────────────────────────────────────────────────

    public EditOrderPage clickMyOrders() {
        log.info("Clicking 'طلباتي'");
        closeAnyModal();
        waitForClickable(myOrdersLink);
        jsClick(myOrdersLink);
        waitForPageLoad();
        log.info("Navigated to orders list.");
        return this;
    }

    // ── Step 3: خيارات الطلب (3 نقاط) ───────────────────────────────────

    public EditOrderPage clickOrderOptions() {
        log.info("Clicking order options (3 dots) for latest order");
        closeAnyModal();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("button.menu-toggle")));
        List<WebElement> btns = driver.findElements(
                By.cssSelector("button.menu-toggle"));
        jsClick(btns.get(0));
        log.info("Order options menu opened.");
        return this;
    }

    // ── Step 4: عرض الطلب ────────────────────────────────────────────────

    public EditOrderPage clickViewOrder() {
        log.info("Clicking 'عرض الطلب'");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(., 'عرض الطلب')]")));
        WebElement btn = driver.findElement(
                By.xpath("//a[contains(., 'عرض الطلب')]"));
        jsClick(btn);
        waitForPageLoad();
        log.info("Navigated to order details page.");
        return this;
    }

    // ── تعديل الطلب ──────────────────────────────────────────────────────

    public EditOrderPage clickEditFromMenu() {
        log.info("Clicking 'تعديل الطلب' from dropdown menu");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("a[href*='/orders/'][href$='/edit']")));
        WebElement btn = driver.findElement(
                By.cssSelector("a[href*='/orders/'][href$='/edit']"));
        jsClick(btn);
        waitForPageLoad();
        closeAnyModal();
        log.info("Navigated to edit order page.");
        return this;
    }

    public EditOrderPage clickAddProduct() {
        log.info("Clicking 'إضافة منتج'");
        closeAnyModal();
        waitForClickable(addProductButton);
        jsClick(addProductButton);
        waitForPageLoad();
        closeAnyModal();
        log.info("Navigated to alternative products page.");
        return this;
    }

    public EditOrderPage clickFirstProduct() {
        log.info("Clicking first product");
        closeAnyModal();
        WebElement firstProduct = waitForClickable(
                By.cssSelector(".product_card__ a.title__"));
        jsClick(firstProduct);
        waitForPageLoad();
        closeAnyModal();
        log.info("Navigated to product details page.");
        return this;
    }

    public EditOrderPage clickAddToOrder() {
        log.info("Clicking 'إضافة إلى الطلب'");
        closeAnyModal();
        waitForClickable(addToOrderButton);
        scrollIntoView(addToOrderButton);
        jsClick(addToOrderButton);
        waitForPageLoad();
        closeAnyModal();
        log.info("Product added to order.");
        return this;
    }

    public EditOrderPage clickCompleteOrder() {
        log.info("Clicking 'إكمال الطلب' (#completeOrderAndAgree)");
        closeAnyModal();
        waitForClickable(completeOrderButton);
        scrollIntoView(completeOrderButton);
        jsClick(completeOrderButton);
        waitForPageLoad();
        closeAnyModal();
        log.info("Navigated to address page.");
        return this;
    }

    public EditOrderPage clickCompleteOrderAddress() {
        log.info("Clicking 'إكمال الطلب' (edit-address)");
        closeAnyModal();
        WebElement btn = waitForClickable(By.xpath(
                "//a[contains(text(),'إكمال الطلب') and contains(@href,'/edit-address')]"));
        jsClick(btn);
        waitForPageLoad();
        closeAnyModal();
        log.info("Order edit completed.");
        return this;
    }

    public boolean isEditOrderButtonVisible() {
        try {
            wait.until(ExpectedConditions
                    .presenceOfElementLocated(By.id("editOrder")));
            log.info("Edit order button is visible. ✅");
            return true;
        } catch (Exception e) {
            log.warn("Edit order button not found: {}", e.getMessage());
            return false;
        }
    }

    // ── إلغاء الطلب ──────────────────────────────────────────────────────

    public EditOrderPage clickCancelOrder() {
        log.info("Clicking 'إلغاء الطلب' button");
        closeAnyModal();
        WebElement btn = waitForClickable(
                By.cssSelector(".cancel_order__"));
        jsClick(btn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[id^='cancelOrderModal']")));
        log.info("Cancel order modal appeared.");
        return this;
    }

    public EditOrderPage clickConfirmCancel() {
        log.info("Clicking confirm cancel button");
        try {
            WebElement btn = waitForClickable(
                    By.cssSelector("button#custom-cancel"));
            jsClick(btn);
            log.info("First confirm cancel clicked.");
        } catch (Exception e) {
            WebElement btn = waitForClickable(
                    By.cssSelector("button[id^='cancelOrder'].red__"));
            jsClick(btn);
            log.info("Second confirm cancel clicked.");
        }
        return this;
    }

    public EditOrderPage enterCancelReason(String reason) {
        log.info("Entering cancel reason: {}", reason);
        WebElement textarea = waitForVisible(
                By.cssSelector("textarea[name='cancel_reason']"));
        textarea.clear();
        textarea.sendKeys(reason);
        log.info("Cancel reason entered.");
        return this;
    }

    public boolean isOrderCancelledSuccessfully() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("orders"),
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".cancel_order__"))
            ));
            log.info("Order cancelled successfully. ✅");
            return true;
        } catch (Exception e) {
            log.warn("Order cancellation not confirmed: {}", e.getMessage());
            return false;
        }
    }
}