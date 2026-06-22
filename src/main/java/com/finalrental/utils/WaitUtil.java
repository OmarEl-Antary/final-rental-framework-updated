package com.finalrental.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtil – static factory for common {@link WebDriverWait} conditions.
 *
 * <p>These methods are convenience wrappers on top of
 * {@link ExpectedConditions}, enabling clean one-liner waits in page objects.
 *
 * <p><strong>No {@code Thread.sleep} is used anywhere in this class.</strong>
 */
public final class WaitUtil {

    private static final Logger log = LogManager.getLogger(WaitUtil.class);

    private WaitUtil() { /* utility class */ }

    // ── Visibility ───────────────────────────────────────────────────────────

    public static WebElement waitForVisible(WebDriver driver, By locator, int timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisible(WebDriver driver, WebElement element, int timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.visibilityOf(element));
    }

    // ── Clickability ─────────────────────────────────────────────────────────

    public static WebElement waitForClickable(WebDriver driver, By locator, int timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    // ── Invisibility ─────────────────────────────────────────────────────────

    public static boolean waitForInvisible(WebDriver driver, By locator, int timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ── Text ─────────────────────────────────────────────────────────────────

    public static boolean waitForTextPresent(WebDriver driver, By locator,
                                             String text, int timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    // ── URL ──────────────────────────────────────────────────────────────────

    public static boolean waitForUrlContains(WebDriver driver, String fragment, int timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.urlContains(fragment));
    }

    // ── Page load ────────────────────────────────────────────────────────────

    public static void waitForDomReady(WebDriver driver, int timeoutSec) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSec)).until(
            (ExpectedCondition<Boolean>) d ->
                "complete".equals(((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState"))
        );
    }

    // ── Loader / Spinner ─────────────────────────────────────────────────────

    /**
     * Waits until a loading overlay / spinner disappears.
     *
     * @param loaderLocator CSS or XPath of the loading indicator
     * @param timeoutSec    maximum seconds to wait
     */
    public static void waitForLoaderToDisappear(WebDriver driver, By loaderLocator, int timeoutSec) {
        log.debug("Waiting for loader to disappear: {}", loaderLocator);
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));
    }

    // ── Alert ────────────────────────────────────────────────────────────────

    public static org.openqa.selenium.Alert waitForAlert(WebDriver driver, int timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.alertIsPresent());
    }
}
