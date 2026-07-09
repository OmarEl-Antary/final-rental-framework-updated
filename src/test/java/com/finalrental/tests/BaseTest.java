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

public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final ConfigReader config = ConfigReader.getInstance();

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

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("▶ Starting test: {}.{}", getClass().getSimpleName(), method.getName());
        DriverFactory.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result, Method method) {
        String testName = getClass().getSimpleName() + "_" + method.getName();

        switch (result.getStatus()) {
            case ITestResult.FAILURE -> {
                log.error("✗ FAILED: {}", testName);
                // screenshot بيبدأ بـ FAILED
                String screenshotPath = ScreenshotUtil.capture("FAILED_" + testName);
                if (screenshotPath != null) {
                    log.info("  Screenshot: {}", screenshotPath);
                }
            }
            case ITestResult.SKIP -> {
                log.warn("⚠ SKIPPED: {}", testName);
            }
            default -> {
                log.info("✔ PASSED: {}", testName);
                // screenshot بيبدأ بـ PASSED
                String screenshotPath = ScreenshotUtil.capture("PASSED_" + testName);
                if (screenshotPath != null) {
                    log.info("  Screenshot: {}", screenshotPath);
                }
            }
        }

        DriverFactory.quitDriver();
        log.info("◀ Test completed: {}", testName);
    }
}