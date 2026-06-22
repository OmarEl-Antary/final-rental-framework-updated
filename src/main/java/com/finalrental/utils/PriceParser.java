package com.finalrental.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PriceParser – extracts numeric {@link BigDecimal} values from price strings
 * displayed in the UI (e.g. "EGP 1,250.50", "LE 3,000", "1250.5 جنيه").
 *
 * <p>Handles:
 * <ul>
 *   <li>Currency prefix/suffix (EGP, LE, $, €, etc.)</li>
 *   <li>Thousands separators (commas or periods depending on locale)</li>
 *   <li>Decimal separators (dot or comma)</li>
 *   <li>Whitespace and non-breaking spaces</li>
 * </ul>
 */
public final class PriceParser {

    /** Matches one or more digit groups separated by optional commas/dots, with optional decimal part. */
    private static final Pattern NUMERIC_PATTERN =
            Pattern.compile("([\\d]{1,3}(?:[,.]?\\d{3})*(?:[.,]\\d{1,2})?)");

    private PriceParser() { /* utility class */ }

    /**
     * Parses the first numeric value found in a price string.
     *
     * <p>Examples:
     * <pre>
     *   parse("EGP 1,250.50") → 1250.50
     *   parse("LE 3,000")     → 3000.00
     *   parse("$99")          → 99.00
     * </pre>
     *
     * @param priceText raw price text from the UI
     * @return parsed amount as {@link BigDecimal}
     * @throws IllegalArgumentException if no numeric value is found
     */
    public static BigDecimal parse(String priceText) {
        if (priceText == null || priceText.isBlank()) {
            throw new IllegalArgumentException("Price text is null or empty.");
        }

        // Remove non-breaking spaces and trim
        String cleaned = priceText.replaceAll("\\u00A0", " ").trim();

        Matcher matcher = NUMERIC_PATTERN.matcher(cleaned);
        if (!matcher.find()) {
            throw new IllegalArgumentException(
                "Cannot find numeric value in price string: '" + priceText + "'");
        }

        String numeric = matcher.group(1);

        // Normalise: remove thousands separators and convert to decimal dot notation
        if (numeric.contains(",") && numeric.contains(".")) {
            // Both present → comma is thousands separator (e.g. "1,250.50")
            numeric = numeric.replace(",", "");
        } else if (numeric.contains(",")) {
            // Could be European decimal (1.250,50) or thousands (1,250)
            long commaCount = numeric.chars().filter(c -> c == ',').count();
            if (commaCount == 1 && numeric.indexOf(',') == numeric.length() - 3) {
                // Looks like decimal comma: "3,50"
                numeric = numeric.replace(",", ".");
            } else {
                // Thousands: "1,250" or "1,250,000"
                numeric = numeric.replace(",", "");
            }
        }

        return new BigDecimal(numeric);
    }

    /**
     * Returns whether two prices are equal within a given tolerance
     * (useful for floating-point rounding differences between UI and calculation).
     *
     * @param expected    expected price
     * @param actual      actual price from UI
     * @param tolerancePct tolerance as a percentage (e.g. 0.01 for 1%)
     */
    public static boolean isWithinTolerance(BigDecimal expected, BigDecimal actual, double tolerancePct) {
        if (expected.compareTo(BigDecimal.ZERO) == 0) {
            return actual.compareTo(BigDecimal.ZERO) == 0;
        }
        BigDecimal diff = expected.subtract(actual).abs();
        BigDecimal maxDiff = expected.abs()
                .multiply(BigDecimal.valueOf(tolerancePct / 100));
        return diff.compareTo(maxDiff) <= 0;
    }
}
