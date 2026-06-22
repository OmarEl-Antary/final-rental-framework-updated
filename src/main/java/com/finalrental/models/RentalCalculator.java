package com.finalrental.models;

import com.finalrental.config.ConfigReader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * RentalCalculator – encapsulates the rental pricing domain logic.
 *
 * <p>All monetary values are represented as {@link BigDecimal} with
 * {@code HALF_UP} rounding to two decimal places to avoid floating-point
 * drift when comparing UI-displayed prices.
 *
 * <p>Formula:
 * <pre>
 *   rentalDays      = checkOut - checkIn  (inclusive start, exclusive end)
 *   subtotal        = dailyRate × rentalDays
 *   taxAmount       = subtotal × (taxRate / 100)
 *   totalAmount     = subtotal + taxAmount
 * </pre>
 */
public class RentalCalculator {

    private static final int SCALE = 2;
    private static final RoundingMode MODE = RoundingMode.HALF_UP;

    private final BigDecimal dailyRate;
    private final LocalDate  checkIn;
    private final LocalDate  checkOut;
    private final BigDecimal taxRate;
    private final String     currency;

    // ── Constructor ──────────────────────────────────────────────────────────

    public RentalCalculator(BigDecimal dailyRate, LocalDate checkIn, LocalDate checkOut) {
        ConfigReader config = ConfigReader.getInstance();
        this.dailyRate = dailyRate.setScale(SCALE, MODE);
        this.checkIn   = checkIn;
        this.checkOut  = checkOut;
        this.taxRate   = BigDecimal.valueOf(config.getRentalTaxRate());
        this.currency  = config.getRentalCurrency();
        validateDates();
    }

    public RentalCalculator(BigDecimal dailyRate, LocalDate checkIn, LocalDate checkOut,
                             double customTaxRate) {
        ConfigReader config = ConfigReader.getInstance();
        this.dailyRate = dailyRate.setScale(SCALE, MODE);
        this.checkIn   = checkIn;
        this.checkOut  = checkOut;
        this.taxRate   = BigDecimal.valueOf(customTaxRate);
        this.currency  = config.getRentalCurrency();
        validateDates();
    }

    // ── Business Logic ───────────────────────────────────────────────────────

    /** Number of rental days (check-out minus check-in). */
    public long getRentalDays() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /** Daily rental rate (before tax). */
    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    /** Subtotal = dailyRate × rentalDays. */
    public BigDecimal getSubtotal() {
        return dailyRate.multiply(BigDecimal.valueOf(getRentalDays()))
                        .setScale(SCALE, MODE);
    }

    /** Tax amount = subtotal × (taxRate / 100). */
    public BigDecimal getTaxAmount() {
        return getSubtotal()
                .multiply(taxRate)
                .divide(BigDecimal.valueOf(100), SCALE, MODE);
    }

    /** Total = subtotal + tax. */
    public BigDecimal getTotalAmount() {
        return getSubtotal().add(getTaxAmount()).setScale(SCALE, MODE);
    }

    /** Tax rate as a percentage (e.g. 14.0 for 14%). */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getCheckIn()  { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }

    // ── Validation ───────────────────────────────────────────────────────────

    private void validateDates() {
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException(
                "Check-in date (" + checkIn + ") must be before check-out date (" + checkOut + ").");
        }
        ConfigReader config = ConfigReader.getInstance();
        long days = getRentalDays();
        int minDays = config.getRentalMinDays();
        if (days < minDays) {
            throw new IllegalArgumentException(
                "Rental period (" + days + " days) is below the minimum of " + minDays + " day(s).");
        }
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
            "RentalCalculator{checkIn=%s, checkOut=%s, days=%d, rate=%s %s, " +
            "subtotal=%s, tax=%.1f%% (%s), total=%s}",
            checkIn, checkOut, getRentalDays(), currency, dailyRate,
            getSubtotal(), taxRate.doubleValue(), getTaxAmount(), getTotalAmount()
        );
    }
}
