package com.finalrental.utils;

import com.finalrental.config.ConfigReader;
import com.finalrental.config.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {}

    public static String capture(String testName) {
        WebDriver driver;
        try {
            driver = DriverFactory.getDriver();
        } catch (IllegalStateException e) {
            log.warn("Cannot capture screenshot: WebDriver is not initialised.");
            return null;
        }

        // تحديد الـ folder بناءً على PASSED أو FAILED
        String baseDir = ConfigReader.getInstance().getScreenshotPath();
        String subFolder = testName.startsWith("PASSED") ? "PASSED/" : "FAILED/";
        String directory = baseDir + subFolder;

        // إنشاء الـ folder لو مش موجود
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String timestamp   = LocalDateTime.now().format(TIMESTAMP);
        String safeLabel   = testName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String fileName    = safeLabel + "_" + timestamp + ".png";
        File   destination = new File(directory + fileName);

        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(source, destination);
            log.info("Screenshot saved: {}", destination.getAbsolutePath());
            return destination.getAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to save screenshot: {}", e.getMessage());
            return null;
        }
    }

    public static String captureAsBase64() {
        try {
            return ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.warn("Could not capture Base64 screenshot: {}", e.getMessage());
            return "";
        }
    }
}