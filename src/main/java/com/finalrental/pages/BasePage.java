package com.finalrental.pages;

import com.finalrental.config.ConfigReader;
import com.finalrental.config.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage – the foundation for every Page Object in the framework.
 *
 * <p>Provides:
 * <ul>
 *   <li>Explicit wait helpers (zero use of {@code Thread.sleep})</li>
 *   <li>Robust click, type, and select utilities</li>
 *   <li>JavaScript fallbacks for edge-case interactions</li>
 *   <li>Scroll, hover, and drag helpers</li>
 * </ul>
 *
 * <p>All subclasses call {@code super()} and receive a fully initialised
 * {@link PageFactory} instance.
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(getClass());

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;
    protected final Actions actions;
    protected final ConfigReader config;

    // ── Constructor ──────────────────────────────────────────────────────────

    protected BasePage() {
        this.driver  = DriverFactory.getDriver();
        this.config  = ConfigReader.getInstance();
        this.wait    = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    public void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
        waitForPageLoad();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    // ── Waits ────────────────────────────────────────────────────────────────

    /**
     * Waits until the element is visible in the DOM and on-screen.
     */
    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the element is clickable (visible + enabled).
     */
    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until the element is present in the DOM (may not be visible).
     */
    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until the element is invisible / removed.
     */
    protected void waitForInvisible(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    protected void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits until the text appears inside the element.
     */
    protected void waitForText(WebElement element, String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElement(element, expectedText));
    }

    /**
     * Waits until the page {@code document.readyState} is "complete".
     */
    protected void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Waits for a URL fragment or full URL to be present.
     */
    protected void waitForUrlContains(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Waits until the count of elements matching the locator equals the expected count.
     */
    protected List<WebElement> waitForElementCount(By locator, int expectedCount) {
        return wait.until(driver -> {
            List<WebElement> elements = driver.findElements(locator);
            return elements.size() == expectedCount ? elements : null;
        });
    }

    // ── Click ────────────────────────────────────────────────────────────────

    /**
     * Standard click with explicit clickability wait.
     */
    protected void click(WebElement element) {
        log.debug("Clicking element: {}", getElementDescription(element));
        waitForClickable(element).click();
    }

    protected void click(By locator) {
        click(waitForClickable(locator));
    }

    /**
     * JavaScript click – use only when the standard click is intercepted.
     */
    protected void jsClick(WebElement element) {
        log.debug("JS-clicking element: {}", getElementDescription(element));
        scrollIntoView(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Clicks and waits for navigation (URL change).
     */
    protected void clickAndWaitForNavigation(WebElement element, String expectedUrlFragment) {
        click(element);
        waitForUrlContains(expectedUrlFragment);
        waitForPageLoad();
    }

    // ── Type ─────────────────────────────────────────────────────────────────

    /**
     * Clears the field and types the given text with an explicit visibility wait.
     */
    protected void type(WebElement element, String text) {
        log.debug("Typing '{}' into element: {}", text, getElementDescription(element));
        WebElement visible = waitForVisible(element);
        visible.clear();
        visible.sendKeys(text);
    }

    protected void type(By locator, String text) {
        type(waitForVisible(locator), text);
    }

    /**
     * Clears a field using keyboard shortcuts – useful for inputs that ignore {@code clear()}.
     */
    protected void clearAndType(WebElement element, String text) {
        waitForClickable(element).click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        element.sendKeys(text);
    }

    // ── Select / Dropdown ────────────────────────────────────────────────────

    protected void selectByVisibleText(WebElement dropdown, String text) {
        waitForVisible(dropdown);
        new Select(dropdown).selectByVisibleText(text);
    }

    protected void selectByValue(WebElement dropdown, String value) {
        waitForVisible(dropdown);
        new Select(dropdown).selectByValue(value);
    }

    protected void selectByIndex(WebElement dropdown, int index) {
        waitForVisible(dropdown);
        new Select(dropdown).selectByIndex(index);
    }

    protected String getSelectedOption(WebElement dropdown) {
        waitForVisible(dropdown);
        return new Select(dropdown).getFirstSelectedOption().getText().trim();
    }

    // ── Text / Attribute Getters ─────────────────────────────────────────────

    protected String getText(WebElement element) {
        return waitForVisible(element).getText().trim();
    }

    protected String getAttribute(WebElement element, String attribute) {
        waitForVisible(element);
        return element.getAttribute(attribute);
    }

    protected String getValue(WebElement element) {
        return getAttribute(element, "value");
    }

    // ── Checkbox / Radio ─────────────────────────────────────────────────────

    protected void check(WebElement checkbox) {
        waitForClickable(checkbox);
        if (!checkbox.isSelected()) click(checkbox);
    }

    protected void uncheck(WebElement checkbox) {
        waitForClickable(checkbox);
        if (checkbox.isSelected()) click(checkbox);
    }

    protected boolean isChecked(WebElement checkbox) {
        return checkbox.isSelected();
    }

    // ── Visibility / State Checks ────────────────────────────────────────────

    public boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected boolean isElementEnabled(WebElement element) {
        return element.isEnabled();
    }

    // ── Scroll ───────────────────────────────────────────────────────────────

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    protected void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0);");
    }

    protected void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight);");
    }

    // ── Hover ────────────────────────────────────────────────────────────────

    protected void hover(WebElement element) {
        waitForVisible(element);
        actions.moveToElement(element).perform();
    }

    // ── Alerts ───────────────────────────────────────────────────────────────

    protected void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    protected void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).dismiss();
    }

    protected String getAlertText() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    // ── Frame Handling ───────────────────────────────────────────────────────

    protected void switchToFrame(WebElement frameElement) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
    }

    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    // ── JavaScript Utilities ─────────────────────────────────────────────────

    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    protected void highlightElement(WebElement element) {
        executeScript(
            "arguments[0].style.border='3px solid red'; arguments[0].style.backgroundColor='yellow';",
            element
        );
    }

    protected void setValueViaJS(WebElement element, String value) {
        executeScript("arguments[0].value=arguments[1];", element, value);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String getElementDescription(WebElement element) {
        try {
            String tag  = element.getTagName();
            String id   = element.getAttribute("id");
            String name = element.getAttribute("name");
            if (id   != null && !id.isBlank())   return tag + "#" + id;
            if (name != null && !name.isBlank())  return tag + "[name=" + name + "]";
            return tag;
        } catch (Exception e) {
            return "<unknown>";
        }
    }
}
