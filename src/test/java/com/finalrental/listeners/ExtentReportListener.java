package com.finalrental.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.finalrental.config.ConfigReader;
import com.finalrental.utils.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * ExtentReportListener – TestNG {@link ITestListener} that builds a rich
 * HTML report using Extent Reports (Spark reporter).
 *
 * <p>The report is flushed to disk in {@link #onFinish(ITestContext)}.
 * Add this listener to {@code testng.xml}:
 * <pre>
 *   &lt;listeners&gt;
 *     &lt;listener class-name="com.finalrental.listeners.ExtentReportListener"/&gt;
 *   &lt;/listeners&gt;
 * </pre>
 */
public class ExtentReportListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(ExtentReportListener.class);
    private static final ConfigReader CONFIG = ConfigReader.getInstance();

    /** One report instance shared across all threads. */
    private static ExtentReports extent;

    /** Thread-local ExtentTest so parallel tests don't cross-contaminate. */
    private static final ThreadLocal<ExtentTest> TEST_NODE = new ThreadLocal<>();

    // ── ITestListener callbacks ───────────────────────────────────────────────

    @Override
    public void onStart(ITestContext context) {
        log.info("Initialising Extent Reports...");

        ExtentSparkReporter spark = new ExtentSparkReporter(CONFIG.getExtentReportPath());
        spark.config().setDocumentTitle(CONFIG.getExtentReportTitle());
        spark.config().setReportName(CONFIG.getExtentReportName());
        spark.config().setTheme(Theme.DARK);
        spark.config().setEncoding("utf-8");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Environment", CONFIG.getEnvironment());
        extent.setSystemInfo("Base URL",    CONFIG.getBaseUrl());
        extent.setSystemInfo("Browser",     CONFIG.getBrowser());
        extent.setSystemInfo("Headless",    String.valueOf(CONFIG.isHeadless()));
        extent.setSystemInfo("Suite",       context.getSuite().getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String[] groups = result.getMethod().getGroups();

        ExtentTest test = extent.createTest(testName, description);
        for (String group : groups) {
            test.assignCategory(group);
        }
        TEST_NODE.set(test);
        log.debug("Extent test started: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TEST_NODE.get().log(Status.PASS, "Test passed.");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = TEST_NODE.get();
        test.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());

        // Attach screenshot
        String base64Screenshot = ScreenshotUtil.captureAsBase64();
        if (!base64Screenshot.isEmpty()) {
            try {
                test.addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
            } catch (Exception e) {
                log.warn("Could not attach screenshot to report: {}", e.getMessage());
            }
        }

        // Log the full stack trace
        test.log(Status.FAIL, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TEST_NODE.get().log(Status.SKIP,
                "Test skipped: " + (result.getThrowable() != null
                        ? result.getThrowable().getMessage() : "No reason given."));
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
            log.info("Extent Report written to: {}", CONFIG.getExtentReportPath());
        }
        TEST_NODE.remove();
    }
}
