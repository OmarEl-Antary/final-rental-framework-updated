package com.finalrental.tests;

import com.finalrental.config.ConfigReader;
import com.finalrental.config.DriverFactory;
import com.finalrental.utils.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * BaseTest – the parent class for every test class in the framework.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>{@code @BeforeSuite} – log suite start and config summary</li>
 *   <li>{@code @BeforeMethod} – initialise a fresh WebDriver per test</li>
 *   <li>{@code @AfterMethod} – capture screenshot on failure, quit driver</li>
 * </ul>
 *
 * <p>Thread-safe via {@link DriverFactory}'s {@code ThreadLocal} design,
 * allowing TestNG parallel execution at the method level.
 */
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final ConfigReader config = ConfigReader.getInstance();

    // ── Suite Setup ──────────────────────────────────────────────────────────

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log.info("========================================================");
        log.info("  Final Rental – E2E Test Suite Starting");
        log.info("  Environment : {}", config.getEnvironment());
        log.info("  Base URL    : {}", config.getBaseUrl());
        log.info("  Browser     : {}", config.getBrowser());
        log.info("  Headless    : {}", config.isHeadless());
        log.info("========================================================");
    }

    // ── Per-Test Setup ───────────────────────────────────────────────────────

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("▶ Starting test: {}.{}", getClass().getSimpleName(), method.getName());
        DriverFactory.initDriver();
    }

    // ── Per-Test Teardown ────────────────────────────────────────────────────

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result, Method method) {
        String testName = getClass().getSimpleName() + "_" + method.getName();

        switch (result.getStatus()) {
            case ITestResult.FAILURE -> {
                log.error("✗ FAILED: {}", testName);
                if (config.isScreenshotOnFailure()) {
                    String screenshotPath = ScreenshotUtil.capture(testName);
                    if (screenshotPath != null) {
                        log.info("  Screenshot: {}", screenshotPath);
                    }
                }
            }
            case ITestResult.SKIP -> log.warn("⚠ SKIPPED: {}", testName);
            default              -> log.info("✔ PASSED: {}", testName);
        }

        DriverFactory.quitDriver();
        log.info("◀ Test completed: {}", testName);
    }
}
