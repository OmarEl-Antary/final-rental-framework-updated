package com.finalrental.bdd.stepdefs;

import com.finalrental.pages.*;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class CancelOrderSteps {

    private static final Logger log =
            LoggerFactory.getLogger(CancelOrderSteps.class);

    private EditOrderPage editOrderPage;

    @When("the user clicks view order from options")
    public void the_user_clicks_view_order_from_options() {
        editOrderPage = new EditOrderPage();
        editOrderPage.clickViewOrder();
        log.info("✔ تم الضغط على عرض الطلب");
    }

    @And("the user clicks cancel order button")
    public void the_user_clicks_cancel_order_button() {
        editOrderPage.clickCancelOrder();
        log.info("✔ تم الضغط على إلغاء الطلب");
    }

    @And("the user clicks confirm cancel")
    public void the_user_clicks_confirm_cancel() {
        editOrderPage.clickConfirmCancel();
        log.info("✔ تم الضغط على تأكيد الإلغاء");
    }

    @And("the user enters cancel reason {string}")
    public void the_user_enters_cancel_reason(String reason) {
        editOrderPage.enterCancelReason(reason);
        log.info("✔ تم كتابة سبب الإلغاء: {}", reason);
    }

    @Then("the order should be cancelled successfully")
    public void the_order_should_be_cancelled_successfully() {
        Assert.assertTrue(editOrderPage.isOrderCancelledSuccessfully(),
                "Order should be cancelled successfully");
        log.info("🎉 تم إلغاء الطلب بنجاح!");
    }
}