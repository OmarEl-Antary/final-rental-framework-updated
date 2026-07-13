package com.finalrental.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Automatically attaches RetryAnalyzer to EVERY @Test method in the suite.
 * This means you do NOT need to add retryAnalyzer = RetryAnalyzer.class
 * manually on each @Test annotation across RegisterTest, LoginOtpTest,
 * OrderFlowTest, EditOrderTest, CancelOrderTest, AddressTest, etc.
 *
 * Registered once in testng.xml alongside ExtentReportListener.
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}