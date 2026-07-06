package com.finalrental.bdd.stepdefs;

import com.finalrental.pages.*;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class EditOrderSteps {

    private static final Logger log =
            LoggerFactory.getLogger(EditOrderSteps.class);

    private EditOrderPage editOrderPage;

    @When("the user clicks on profile link")
    public void the_user_clicks_on_profile_link() {
        editOrderPage = new EditOrderPage();
        editOrderPage.clickProfile();
        log.info("✔ تم الضغط على بيانات حسابي");
    }

    @And("the user clicks on my orders")
    public void the_user_clicks_on_my_orders() {
        editOrderPage.clickMyOrders();
        log.info("✔ تم الضغط على طلباتي");
    }

    @And("the user clicks order options menu")
    public void the_user_clicks_order_options_menu() {
        editOrderPage.clickOrderOptions();
        log.info("✔ تم فتح خيارات الطلب");
    }

    @And("the user clicks edit order from options")
    public void the_user_clicks_edit_order_from_options() {
        editOrderPage.clickEditFromMenu();
        log.info("✔ تم الضغط على تعديل الطلب");
    }

    @And("clicks add product button")
    public void clicks_add_product_button() {
        editOrderPage.clickAddProduct();
        log.info("✔ تم الضغط على إضافة منتج");
    }

    @And("clicks the first product")
    public void clicks_the_first_product() {
        editOrderPage.clickFirstProduct();
        log.info("✔ تم اختيار أول منتج");
    }

    @And("clicks add to order button")
    public void clicks_add_to_order_button() {
        editOrderPage.clickAddToOrder();
        log.info("✔ تم إضافة المنتج للطلب");
    }

    @And("clicks complete order button")
    public void clicks_complete_order_button() {
        editOrderPage.clickCompleteOrder();
        log.info("✔ تم الضغط على إكمال الطلب الأول");
    }

    @And("clicks complete order address button")
    public void clicks_complete_order_address_button() {
        editOrderPage.clickCompleteOrderAddress();
        log.info("✔ تم الضغط على إكمال الطلب الثاني");
    }

    @Then("the edit order button should be visible")
    public void the_edit_order_button_should_be_visible() {
        Assert.assertTrue(editOrderPage.isEditOrderButtonVisible(),
                "Edit order button should be visible after successful edit");
        log.info("🎉 تم تعديل الطلب بنجاح!");
    }
}