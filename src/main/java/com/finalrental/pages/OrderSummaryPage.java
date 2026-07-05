package com.finalrental.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OrderSummaryPage extends BasePage {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.15");

    /**
     * استخراج قيمة عنصر من ملخص الطلب بالنص
     */
    private BigDecimal getValueByTitle(String title) {
        try {
            WebElement element = driver.findElement(By.xpath(
                    "//div[contains(@class,'bill_item__')]" +
                            "[p[contains(text(),'" + title + "')]]" +
                            "//p[contains(@class,'bill_price__')]"
            ));
            String text = element.getText()
                    .replaceAll("[^\\d.]", "")
                    .trim();
            return new BigDecimal(text.isEmpty() ? "0" : text);
        } catch (Exception e) {
            log.debug("Value not found for title '{}': {}", title, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getRentalPrice() {
        BigDecimal val = getValueByTitle("قيمة الايجار");
        log.info("قيمة الإيجار: {}", val);
        return val;
    }

    public BigDecimal getDeliveryPrice() {
        BigDecimal val = getValueByTitle("قيمة التوصيل");
        log.info("قيمة التوصيل: {}", val);
        return val;
    }

    public BigDecimal getDiscountPrice() {
        try {
            WebElement element = driver.findElement(By.cssSelector(
                    ".bill_item__.discount__ .bill_price__"));
            String text = element.getText()
                    .replaceAll("[^\\d.]", "")
                    .trim();
            BigDecimal val = new BigDecimal(text.isEmpty() ? "0" : text);
            log.info("قيمة الخصم: {}", val);
            return val;
        } catch (Exception e) {
            log.info("لا يوجد خصم");
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTaxPrice() {
        BigDecimal val = getValueByTitle("قيمة الضريبة");
        log.info("قيمة الضريبة: {}", val);
        return val;
    }

    public BigDecimal getTotalPrice() {
        try {
            WebElement element = driver.findElement(By.cssSelector(
                    ".bill_item__.total__ .bill_price__"));
            String text = element.getText()
                    .replaceAll("[^\\d.]", "")
                    .trim();
            BigDecimal val = new BigDecimal(text.isEmpty() ? "0" : text);
            log.info("المبلغ الكلي: {}", val);
            return val;
        } catch (Exception e) {
            log.warn("لم يتم إيجاد المبلغ الكلي: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * التحقق من صحة حساب الضريبة
     * الضريبة = 15% × (الإيجار + التوصيل - الخصم)
     */
    public boolean isTaxCalculationCorrect() {
        BigDecimal rental   = getRentalPrice();
        BigDecimal delivery = getDeliveryPrice();
        BigDecimal discount = getDiscountPrice();
        BigDecimal tax      = getTaxPrice();

        BigDecimal expectedTax = rental.add(delivery)
                .subtract(discount)
                .multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal actualTax = tax.setScale(2, RoundingMode.HALF_UP);

        log.info("الضريبة المتوقعة: {} | الضريبة الفعلية: {}",
                expectedTax, actualTax);

        return expectedTax.compareTo(actualTax) == 0;
    }

    /**
     * التحقق من صحة المبلغ الكلي
     * الإجمالي = الإيجار + التوصيل - الخصم + الضريبة
     */
    public boolean isTotalCalculationCorrect() {
        BigDecimal rental   = getRentalPrice();
        BigDecimal delivery = getDeliveryPrice();
        BigDecimal discount = getDiscountPrice();
        BigDecimal tax      = getTaxPrice();
        BigDecimal total    = getTotalPrice();

        BigDecimal expectedTotal = rental.add(delivery)
                .subtract(discount)
                .add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal actualTotal = total.setScale(2, RoundingMode.HALF_UP);

        log.info("الإجمالي المتوقع: {} | الإجمالي الفعلي: {}",
                expectedTotal, actualTotal);

        return expectedTotal.compareTo(actualTotal) == 0;
    }

    public boolean isSummaryVisible() {
        return isElementPresent(By.cssSelector(".order_summary"));
    }
}