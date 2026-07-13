package com.finalrental.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.finalrental.utils.ScreenshotUtil;
import org.apache.commons.io.FileUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
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
            return src.getName(); // relative path
        } catch (Exception e) {
            return screenshotPath;
        }
    }

    @Override
    public void onStart(ITestContext context) {
        createDirectories(BASE_PATH + "PASSED/");
        createDirectories(BASE_PATH + "FAILED/");

        extent = new ExtentReports();
        extent.setSystemInfo("Project",     "Final Rental E2E Framework");
        extent.setSystemInfo("Environment", "Testing");
        extent.setSystemInfo("Base URL",    "https://testing.final.sa");
        extent.setSystemInfo("Browser",     "Chrome");
        extent.setSystemInfo("Java",        System.getProperty("java.version"));
        extent.setSystemInfo("OS",          System.getProperty("os.name"));
        extent.setSystemInfo("Executed By", System.getProperty("user.name"));
        extent.setSystemInfo("Executed At",
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")));
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) extent.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName    = result.getMethod().getMethodName();
        String className   = result.getTestClass().getName()
                .replace("com.finalrental.tests.", "");
        String description = result.getMethod().getDescription();

        ExtentTest extentTest = extent.createTest(
                "<b>" + className + "</b> → " + testName,
                description != null ? description : ""
        );

        for (String group : result.getMethod().getGroups()) {
            extentTest.assignCategory(group);
        }

        test.set(extentTest);
        test.get().info("Test Started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String className  = result.getTestClass().getName()
                .replace("com.finalrental.tests.", "");
        String timestamp  = LocalDateTime.now().format(FMT);
        String reportDir  = BASE_PATH + "PASSED/";
        String reportPath = reportDir + "PASSED-" + className + "-" + timestamp + ".html";

        createDirectories(reportDir);

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("PASSED - " + className);
        spark.config().setReportName("✅ " + className);
        extent.attachReporter(spark);

        String screenshotPath = ScreenshotUtil.capture(
                "PASSED_" + result.getMethod().getMethodName());
        String relativePath = copyScreenshotToReportDir(screenshotPath, reportDir);

        test.get().pass("✅ Test PASSED");

        if (relativePath != null) {
            try {
                test.get().pass("Screenshot",
                        MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            } catch (Exception e) {
                test.get().info("Screenshot: " + relativePath);
            }
        }

        extent.flush();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String className  = result.getTestClass().getName()
                .replace("com.finalrental.tests.", "");
        String timestamp  = LocalDateTime.now().format(FMT);
        String reportDir  = BASE_PATH + "FAILED/";

        String failedStep = "UnknownStep";
        if (result.getThrowable() != null && result.getThrowable().getMessage() != null) {
            failedStep = result.getThrowable().getMessage()
                    .replaceAll("[^a-zA-Z0-9\u0600-\u06FF_]", "_");
            if (failedStep.length() > 50) failedStep = failedStep.substring(0, 50);
        }

        String reportPath = reportDir + "FAILED-" + className + "-" + timestamp
                + "-" + failedStep + ".html";

        createDirectories(reportDir);

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("FAILED - " + className);
        spark.config().setReportName("❌ " + className);
        extent.attachReporter(spark);

        test.get().fail("❌ Test FAILED");
        test.get().fail(result.getThrowable());

        String screenshotPath = ScreenshotUtil.capture(
                "FAILED_" + result.getMethod().getMethodName());
        String relativePath = copyScreenshotToReportDir(screenshotPath, reportDir);

        if (relativePath != null) {
            try {
                test.get().fail("Screenshot at failure",
                        MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());
            } catch (Exception e) {
                test.get().info("Screenshot: " + relativePath);
            }
        }

        extent.flush();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().skip("⚠️ Test SKIPPED");
        if (result.getThrowable() != null) {
            test.get().skip(result.getThrowable());
        }
    }

    public static ExtentTest getTest() {
        return test.get();
    }
}