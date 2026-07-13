package com.finalrental.bdd.stepdefs;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.finalrental.config.DriverFactory;
import com.finalrental.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    private static final String BASE_PATH = "test-output/reports/";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy_hh-mm-a");

    private void createDirectories(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String copyScreenshotToReportDir(String screenshotPath, String reportDir) {
        if (screenshotPath == null) return null;
        try {
            File src  = new File(screenshotPath);
            File dest = new File(reportDir + src.getName());
            FileUtils.copyFile(src, dest);
            return src.getName();
        } catch (Exception e) {
            return screenshotPath;
        }
    }

    @Before
    public void setUp() {
        createDirectories(BASE_PATH + "PASSED/");
        createDirectories(BASE_PATH + "FAILED/");
        DriverFactory.initDriver();
        try {
            DriverFactory.getDriver().manage().deleteAllCookies();
        } catch (Exception ignored) {}
    }

    @After
    public void tearDown(Scenario scenario) {
        String scenarioName = scenario.getName()
                .replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String timestamp = LocalDateTime.now().format(FMT);

        if (scenario.isFailed()) {
            String reportDir  = BASE_PATH + "FAILED/";
            String reportPath = reportDir + "FAILED-" + scenarioName
                    + "-" + timestamp + ".html";

            String screenshotPath = ScreenshotUtil.capture("FAILED_" + scenarioName);
            String relativePath   = copyScreenshotToReportDir(screenshotPath, reportDir);

            ExtentReports extentFailed       = new ExtentReports();
            ExtentSparkReporter sparkFailed  = new ExtentSparkReporter(reportPath);
            sparkFailed.config().setTheme(Theme.DARK);
            sparkFailed.config().setDocumentTitle("FAILED - " + scenarioName);
            sparkFailed.config().setReportName("❌ " + scenario.getName());
            extentFailed.attachReporter(sparkFailed);
            extentFailed.setSystemInfo("Feature", scenario.getUri().toString());
            extentFailed.setSystemInfo("Executed At",
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")));

            ExtentTest extentTest = extentFailed.createTest(scenario.getName());
            extentTest.fail("❌ Scenario FAILED: " + scenario.getName());

            if (relativePath != null) {
                try {
                    extentTest.fail("Screenshot at failure",
                            MediaEntityBuilder.createScreenCaptureFromPath(
                                    relativePath).build());
                } catch (Exception e) {
                    extentTest.info("Screenshot: " + relativePath);
                }
            }

            extentFailed.flush();

        } else {
            String reportDir  = BASE_PATH + "PASSED/";
            String reportPath = reportDir + "PASSED-" + scenarioName
                    + "-" + timestamp + ".html";

            String screenshotPath = ScreenshotUtil.capture("PASSED_" + scenarioName);
            String relativePath   = copyScreenshotToReportDir(screenshotPath, reportDir);

            ExtentReports extentPassed      = new ExtentReports();
            ExtentSparkReporter sparkPassed = new ExtentSparkReporter(reportPath);
            sparkPassed.config().setTheme(Theme.DARK);
            sparkPassed.config().setDocumentTitle("PASSED - " + scenarioName);
            sparkPassed.config().setReportName("✅ " + scenario.getName());
            extentPassed.attachReporter(sparkPassed);
            extentPassed.setSystemInfo("Feature", scenario.getUri().toString());
            extentPassed.setSystemInfo("Executed At",
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")));

            ExtentTest extentTest = extentPassed.createTest(scenario.getName());
            extentTest.pass("✅ Scenario PASSED: " + scenario.getName());

            if (relativePath != null) {
                try {
                    extentTest.pass("Screenshot",
                            MediaEntityBuilder.createScreenCaptureFromPath(
                                    relativePath).build());
                } catch (Exception e) {
                    extentTest.info("Screenshot: " + relativePath);
                }
            }

            extentPassed.flush();
        }

        DriverFactory.quitDriver();
    }
}