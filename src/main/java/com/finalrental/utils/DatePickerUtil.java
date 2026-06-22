package com.finalrental.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * DatePickerUtil – handles common calendar / date-picker widget interactions
 * found on rental websites (month-navigation, day-click, range selection).
 *
 * <p>Strategy:
 * <ol>
 *   <li>Navigate the calendar header month/year until the target month is visible.</li>
 *   <li>Click the correct day cell.</li>
 *   <li>Repeat for the second date in a range picker.</li>
 * </ol>
 *
 * <p>All locators are configurable so the same helper works with different
 * date-picker libraries (Pikaday, Flatpickr, react-datepicker, etc.).
 */
public class DatePickerUtil {

    private static final Logger log = LogManager.getLogger(DatePickerUtil.class);

    private final WebDriver     driver;
    private final WebDriverWait wait;

    // Default CSS selectors – override via setter methods for different pickers
    private String calendarContainerCss  = ".datepicker";
    private String prevMonthButtonCss    = ".datepicker-prev, [data-direction='-1']";
    private String nextMonthButtonCss    = ".datepicker-next, [data-direction='1']";
    private String currentMonthYearCss   = ".datepicker-month-year, .calendar-header";
    private String dayCellCssTpl        = "td[data-date='%s'], [data-day='%s']";
    private String activeDayCss         = ".datepicker-active, .is-selected";

    public DatePickerUtil(WebDriver driver, int waitSeconds) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
    }

    // ── Setter API for custom picker configurations ───────────────────────────

    public DatePickerUtil withContainerCss(String css)      { this.calendarContainerCss = css; return this; }
    public DatePickerUtil withPrevButtonCss(String css)     { this.prevMonthButtonCss   = css; return this; }
    public DatePickerUtil withNextButtonCss(String css)     { this.nextMonthButtonCss   = css; return this; }
    public DatePickerUtil withMonthYearHeaderCss(String css){ this.currentMonthYearCss  = css; return this; }
    public DatePickerUtil withDayCellCssTpl(String cssTpl)  { this.dayCellCssTpl       = cssTpl; return this; }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Selects a single date in an open calendar widget.
     *
     * @param targetDate the date to select
     */
    public void selectDate(LocalDate targetDate) {
        log.info("Selecting date: {}", targetDate);
        waitForCalendarVisible();
        navigateToMonth(targetDate);
        clickDay(targetDate);
    }

    /**
     * Selects a date range (check-in → check-out) in a range picker.
     * The calendar is expected to stay open between the two clicks.
     *
     * @param checkIn  start date
     * @param checkOut end date
     */
    public void selectDateRange(LocalDate checkIn, LocalDate checkOut) {
        log.info("Selecting date range: {} → {}", checkIn, checkOut);
        waitForCalendarVisible();
        navigateToMonth(checkIn);
        clickDay(checkIn);
        navigateToMonth(checkOut);
        clickDay(checkOut);
        log.info("Date range selected successfully.");
    }

    /**
     * Types dates directly into input fields (for text-input style pickers).
     *
     * @param inputElement the date input field
     * @param date         the date to type
     * @param format       format string, e.g. "MM/dd/yyyy"
     */
    public void typeDateIntoInput(WebElement inputElement, LocalDate date, String format) {
        String formatted = DateTimeFormatter.ofPattern(format).format(date);
        log.info("Typing date '{}' into input field.", formatted);
        wait.until(ExpectedConditions.elementToBeClickable(inputElement)).clear();
        inputElement.sendKeys(formatted);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    /**
     * Clicks the previous/next arrow until the calendar header shows
     * the target month and year.  Caps at 24 iterations to avoid infinite loops.
     */
    private void navigateToMonth(LocalDate targetDate) {
        int maxIterations = 24;
        for (int i = 0; i < maxIterations; i++) {
            String headerText = getCurrentMonthYearText();
            if (isTargetMonthVisible(headerText, targetDate)) {
                log.debug("Calendar is showing correct month: {}", headerText);
                return;
            }
            if (needsForwardNavigation(headerText, targetDate)) {
                clickNext();
            } else {
                clickPrev();
            }
        }
        throw new RuntimeException("Failed to navigate calendar to: " + targetDate +
                " after " + maxIterations + " attempts.");
    }

    private void clickDay(LocalDate date) {
        String dayStr = String.valueOf(date.getDayOfMonth());
        // Try data-date attribute first (ISO format), then day number
        String isoDate = DateTimeFormatter.ISO_LOCAL_DATE.format(date);
        String css = String.format(dayCellCssTpl, isoDate, dayStr);

        try {
            WebElement dayCell = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(css)));
            dayCell.click();
            log.debug("Clicked day: {}", date);
        } catch (Exception e) {
            // Fallback: find by visible text in day cells
            log.debug("CSS selector failed, falling back to text search for day: {}", dayStr);
            clickDayByText(dayStr);
        }
    }

    private void clickDayByText(String dayText) {
        By dayLocator = By.xpath(
            "//td[not(contains(@class,'disabled')) and not(contains(@class,'other-month'))" +
            " and normalize-space(text())='" + dayText + "']" +
            " | //div[contains(@class,'day') and normalize-space(text())='" + dayText + "'" +
            " and not(contains(@class,'disabled'))]"
        );
        WebElement day = wait.until(ExpectedConditions.elementToBeClickable(dayLocator));
        day.click();
    }

    private void clickNext() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(nextMonthButtonCss)));
        btn.click();
        waitForCalendarToRefresh();
    }

    private void clickPrev() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(prevMonthButtonCss)));
        btn.click();
        waitForCalendarToRefresh();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void waitForCalendarVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(calendarContainerCss)));
    }

    private void waitForCalendarToRefresh() {
        // Short pause for calendar animation; NOT Thread.sleep – uses WebDriverWait
        try {
            wait.until(driver ->
                !getCurrentMonthYearText().isEmpty()
            );
        } catch (Exception ignored) { /* header is always present */ }
    }

    private String getCurrentMonthYearText() {
        try {
            WebElement header = driver.findElement(By.cssSelector(currentMonthYearCss));
            return header.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private boolean isTargetMonthVisible(String headerText, LocalDate targetDate) {
        String targetMonth = targetDate.getMonth()
                                       .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String targetYear  = String.valueOf(targetDate.getYear());
        String headerLower = headerText.toLowerCase();
        return headerLower.contains(targetMonth.toLowerCase()) &&
               headerLower.contains(targetYear);
    }

    private boolean needsForwardNavigation(String headerText, LocalDate targetDate) {
        // Parse month/year from header; default to "go forward" if parsing fails
        try {
            // Common header formats: "June 2025", "Jun 2025", "06/2025"
            LocalDate headerDate = parseHeaderDate(headerText);
            return targetDate.isAfter(headerDate.withDayOfMonth(1));
        } catch (Exception e) {
            return true;
        }
    }

    private LocalDate parseHeaderDate(String header) {
        // Try "MMMM yyyy"
        for (Month month : Month.values()) {
            String fullName  = month.getDisplayName(TextStyle.FULL,  Locale.ENGLISH);
            String shortName = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            if (header.contains(fullName) || header.contains(shortName)) {
                // Extract year (4 consecutive digits)
                String yearStr = header.replaceAll("[^0-9]", "").substring(0, 4);
                int year = Integer.parseInt(yearStr);
                return LocalDate.of(year, month, 1);
            }
        }
        throw new IllegalArgumentException("Cannot parse calendar header date: " + header);
    }
}
