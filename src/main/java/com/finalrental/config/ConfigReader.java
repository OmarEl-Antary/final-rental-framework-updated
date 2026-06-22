package com.finalrental.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader – thread-safe singleton that loads {@code config.properties}
 * once and exposes typed getters for every framework setting.
 *
 * <p>Any property can be overridden at runtime with a Maven system property:
 * {@code mvn test -Dbrowser=firefox -Denv=staging}
 */
public final class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE = "src/test/resources/config.properties";

    private static volatile ConfigReader instance;
    private final Properties props = new Properties();

    // ── Singleton ────────────────────────────────────────────────────────────

    private ConfigReader() {
        loadProperties();
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    // ── Loading ──────────────────────────────────────────────────────────────

    private void loadProperties() {
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
            log.info("Loaded configuration from: {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config.properties at: " + CONFIG_FILE, e);
        }
    }

    /**
     * Returns the value for {@code key}, giving precedence to JVM system
     * properties so Maven {@code -D} overrides always win.
     */
    private String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String value = props.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing required config key: " + key);
        }
        return value.trim();
    }

    private String getOrDefault(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    // ── Environment ──────────────────────────────────────────────────────────

    public String getBaseUrl()      { return get("base.url"); }
    public String getEnvironment()  { return get("env"); }

    // ── Browser ──────────────────────────────────────────────────────────────

    public String getBrowser()      { return get("browser").toLowerCase(); }
    public boolean isHeadless()     { return Boolean.parseBoolean(get("headless")); }

    // ── Timeouts ─────────────────────────────────────────────────────────────

    public int getImplicitWait()    { return Integer.parseInt(get("implicit.wait")); }
    public int getExplicitWait()    { return Integer.parseInt(get("explicit.wait")); }
    public int getPageLoadTimeout() { return Integer.parseInt(get("page.load.timeout")); }
    public int getScriptTimeout()   { return Integer.parseInt(get("script.timeout")); }

    // ── Screenshots ──────────────────────────────────────────────────────────

    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(get("screenshot.on.failure"));
    }
    public String getScreenshotPath() { return get("screenshot.path"); }

    // ── Reports ──────────────────────────────────────────────────────────────

    public String getExtentReportPath()  { return get("extent.report.path"); }
    public String getExtentReportTitle() { return get("extent.report.title"); }
    public String getExtentReportName()  { return get("extent.report.name"); }

    // ── Test Data ────────────────────────────────────────────────────────────

    public String getTestDataPath() { return get("test.data.path"); }

    // ── Rental Business ──────────────────────────────────────────────────────

    public int    getRentalMinDays()  { return Integer.parseInt(get("rental.min.days")); }
    public double getRentalTaxRate()  { return Double.parseDouble(get("rental.tax.rate")); }
    public String getRentalCurrency() { return get("rental.currency"); }
    public String getRentalDateFormat(){ return get("rental.date.format"); }
}
