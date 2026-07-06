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

    // ── Step 4: تعديل الطلب من القايمة ──────────────────────────────────

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

    // ── Step 5: إضافة منتج ───────────────────────────────────────────────

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

    // ── Step 6: اختيار أول منتج ──────────────────────────────────────────

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

    // ── Step 7: إضافة إلى الطلب ─────────────────────────────────────────

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

    // ── Step 8: إكمال الطلب الأول ────────────────────────────────────────

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

    // ── Step 9: إكمال الطلب الثاني ───────────────────────────────────────

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

    // ── التحقق من نجاح التعديل ───────────────────────────────────────────

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
}