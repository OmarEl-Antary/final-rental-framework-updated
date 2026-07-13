package com.finalrental.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Smart Retry Analyzer for TestNG.
 *
 * Retries a test ONLY when the failure looks transient / environment-related
 * (slow site, stale element, timeout, etc.) — the kind of flakiness we know
 * happens with CartPage.clickSubmitCart() and similar slow-response steps.
 *
 * It does NOT retry on AssertionError / real logic failures, so a genuine bug
 * fails fast and clearly instead of being masked by a "passed on retry" report.
 *
 * Max retry attempts is configurable via the system property "retry.count"
 * (e.g. -Dretry.count=3). Default is 2 total attempts (1 retry).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);

    // Default: original run + 1 retry = 2 total attempts.
    private static final int DEFAULT_MAX_RETRY_COUNT = 2;

    // Per-test-method retry counters, keyed by a unique test signature.
    private static final ConcurrentHashMap<String, AtomicInteger> retryCounts = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        String testKey = buildTestKey(result);
        int maxRetryCount = resolveMaxRetryCount();

        AtomicInteger attemptCounter = retryCounts.computeIfAbsent(testKey, k -> new AtomicInteger(0));

        if (!isTransientFailure(result)) {
            log.warn("✗ NOT retrying [{}] — failure looks like a real bug/assertion issue, not a transient one. Cause: {}",
                    testKey, rootCauseSummary(result));
            return false;
        }

        int attemptsSoFar = attemptCounter.get();
        if (attemptsSoFar < maxRetryCount - 1) {
            attemptCounter.incrementAndGet();
            log.warn("↻ Retrying [{}] — attempt {} of {} (transient failure detected: {})",
                    testKey, attemptsSoFar + 2, maxRetryCount, rootCauseSummary(result));
            return true;
        }

        log.error("✗ Giving up on [{}] after {} attempts.", testKey, maxRetryCount);
        return false;
    }

    /**
     * Decides whether the failure is "transient" (worth retrying) or a real
     * bug (should NOT be retried).
     */
    private boolean isTransientFailure(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable == null) {
            return false;
        }

        // Walk the cause chain — sometimes the transient exception is wrapped.
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof TimeoutException
                    || current instanceof StaleElementReferenceException
                    || current instanceof NoSuchElementException
                    || (current instanceof WebDriverException && !(current instanceof AssertionError))) {
                return true;
            }
            // Explicit real-bug failures: never retry these.
            if (current instanceof AssertionError) {
                return false;
            }
            current = current.getCause();
        }

        return false;
    }

    private int resolveMaxRetryCount() {
        String override = System.getProperty("retry.count");
        if (override != null && !override.isBlank()) {
            try {
                int parsed = Integer.parseInt(override.trim());
                if (parsed >= 1) {
                    return parsed;
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid -Dretry.count value '{}', falling back to default ({})", override, DEFAULT_MAX_RETRY_COUNT);
            }
        }
        return DEFAULT_MAX_RETRY_COUNT;
    }

    private String buildTestKey(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    private String rootCauseSummary(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable == null) {
            return "unknown";
        }
        return throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
    }
}