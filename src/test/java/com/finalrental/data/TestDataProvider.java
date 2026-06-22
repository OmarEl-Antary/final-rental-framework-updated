package com.finalrental.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalrental.config.ConfigReader;
import com.finalrental.models.RentalSearchRequest;
import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

/**
 * TestDataProvider – centralised source for test data in the framework.
 *
 * <p>Provides two kinds of data:
 * <ol>
 *   <li><b>Static data</b> – loaded from JSON files in {@code src/test/resources/testdata/}</li>
 *   <li><b>Dynamic data</b> – generated on the fly with {@link Faker} for uniqueness</li>
 * </ol>
 */
public final class TestDataProvider {

    private static final Logger log = LogManager.getLogger(TestDataProvider.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Faker FAKER = new Faker(new Locale("en"));
    private static final ConfigReader CONFIG = ConfigReader.getInstance();

    private TestDataProvider() { /* utility class */ }

    // ── Dynamic Data ──────────────────────────────────────────────────────────

    public static String randomFirstName()  { return FAKER.name().firstName(); }
    public static String randomLastName()   { return FAKER.name().lastName(); }
    public static String randomEmail()      { return FAKER.internet().emailAddress(); }
    public static String randomEgyptianPhone() {
        return "010" + FAKER.number().digits(8);
    }
    public static String randomNationalId() { return FAKER.number().digits(14); }

    /** Generates a valid Egyptian driver's licence format (mock). */
    public static String randomLicenseNumber() {
        return FAKER.number().digits(6) + "/" + FAKER.number().digits(4);
    }

    // ── Rental Search Requests ────────────────────────────────────────────────

    /** Standard 3-day Cairo search starting 5 days from now. */
    public static RentalSearchRequest standardCairoSearch() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        return new RentalSearchRequest.Builder()
                .location("Cairo, Egypt")
                .checkIn(checkIn)
                .checkOut(checkIn.plusDays(3))
                .pickupTime("10:00 AM")
                .dropoffTime("10:00 AM")
                .build();
    }

    /** One-week SUV search. */
    public static RentalSearchRequest weeklyAlexandriaSearch() {
        LocalDate checkIn = LocalDate.now().plusDays(7);
        return new RentalSearchRequest.Builder()
                .location("Alexandria, Egypt")
                .checkIn(checkIn)
                .checkOut(checkIn.plusDays(7))
                .carCategory("SUV")
                .build();
    }

    /** Minimum rental period search (1 day). */
    public static RentalSearchRequest minimumPeriodSearch() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        return new RentalSearchRequest.Builder()
                .location("Cairo, Egypt")
                .checkIn(checkIn)
                .checkOut(checkIn.plusDays(CONFIG.getRentalMinDays()))
                .build();
    }

    /** Long rental – 30 days. */
    public static RentalSearchRequest monthlyRentalSearch() {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        return new RentalSearchRequest.Builder()
                .location("Cairo, Egypt")
                .checkIn(checkIn)
                .checkOut(checkIn.plusDays(30))
                .build();
    }

    // ── Static Data from JSON ─────────────────────────────────────────────────

    /**
     * Loads a JSON file from the test data directory and returns it as a Map.
     *
     * @param fileName e.g. "users.json"
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadJson(String fileName) {
        String path = CONFIG.getTestDataPath() + fileName;
        try {
            return MAPPER.readValue(new File(path), Map.class);
        } catch (Exception e) {
            log.error("Cannot load test data file: {}", path, e);
            throw new RuntimeException("Failed to load test data: " + path, e);
        }
    }

    /**
     * Returns the "valid" user credentials from {@code users.json}.
     * Falls back to default values if file is not present.
     */
    public static String[] getValidUserCredentials() {
        try {
            Map<String, Object> data = loadJson("users.json");
            @SuppressWarnings("unchecked")
            Map<String, String> validUser = (Map<String, String>) data.get("validUser");
            return new String[]{ validUser.get("email"), validUser.get("password") };
        } catch (Exception e) {
            log.warn("users.json not found; using default test credentials.");
            return new String[]{ "testuser@finalrental.com", "Test@1234" };
        }
    }
}
