package com.finalrental.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverFactory – creates and stores one {@link WebDriver} per thread,
 * enabling safe parallel test execution with TestNG.
 *
 * <p>Usage:
 * <pre>
 *   DriverFactory.initDriver();          // in @BeforeMethod
 *   WebDriver driver = DriverFactory.getDriver();
 *   DriverFactory.quitDriver();          // in @AfterMethod
 * </pre>
 */
public final class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    /** One driver per thread – essential for parallel execution. */
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverFactory() { /* utility class */ }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Initialises a new WebDriver for the current thread based on
     * {@link ConfigReader} settings.
     */
    public static void initDriver() {
        ConfigReader config = ConfigReader.getInstance();
        String browser    = config.getBrowser();
        boolean headless  = config.isHeadless();

        log.info("Initialising {} driver | headless={}", browser, headless);

        WebDriver driver = switch (browser) {
            case "chrome" -> createChromeDriver(headless);
            case "firefox" -> createFirefoxDriver(headless);
            case "edge"   -> createEdgeDriver(headless);
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: '" + browser + "'. Use chrome | firefox | edge.");
        };

        applyTimeouts(driver, config);
        driver.manage().window().maximize();
        DRIVER_THREAD_LOCAL.set(driver);
        log.info("WebDriver ready: {}", driver.getClass().getSimpleName());
    }

    /**
     * Returns the WebDriver for the current thread.
     *
     * @throws IllegalStateException if {@link #initDriver()} was not called first
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is not initialised for thread: " + Thread.currentThread().getName() +
                    ". Call DriverFactory.initDriver() in @BeforeMethod.");
        }
        return driver;
    }

    /**
     * Quits the driver and removes it from the thread-local store.
     * Safe to call even if the driver was never initialised.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("WebDriver quit successfully.");
            } catch (Exception e) {
                log.warn("Error while quitting WebDriver: {}", e.getMessage());
            } finally {
                DRIVER_THREAD_LOCAL.remove();
            }
        }
    }

    // ── Browser Creators ─────────────────────────────────────────────────────

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-blink-features=AutomationControlled",
            "--disable-infobars",
            "--window-size=1920,1080",
            "--remote-allow-origins=*"
        );
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("--headless");
        options.addArguments("--width=1920", "--height=1080");
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        return new EdgeDriver(options);
    }

    // ── Timeouts ─────────────────────────────────────────────────────────────

    private static void applyTimeouts(WebDriver driver, ConfigReader config) {
        driver.manage().timeouts()
              .implicitlyWait(Duration.ofSeconds(config.getImplicitWait()))
              .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()))
              .scriptTimeout(Duration.ofSeconds(config.getScriptTimeout()));
    }
}
