package com.finalrental.bdd.stepdefs;

import com.finalrental.config.DriverFactory;
import com.finalrental.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    @Before
    public void setUp() {
        DriverFactory.initDriver();
        try {
            DriverFactory.getDriver().manage().deleteAllCookies();
        } catch (Exception ignored) {}
    }

    @After
    public void tearDown(Scenario scenario) {
        String scenarioName = scenario.getName()
                .replaceAll("[^a-zA-Z0-9_\\-]", "_");

        if (scenario.isFailed()) {
            ScreenshotUtil.capture("FAILED_" + scenarioName);
        } else {
            ScreenshotUtil.capture("PASSED_" + scenarioName);
        }

        DriverFactory.quitDriver();
    }
}