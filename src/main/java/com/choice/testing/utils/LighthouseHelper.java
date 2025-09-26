package com.choice.testing.utils;

import com.choice.testing.drivers.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to easily integrate Lighthouse audits into existing Selenium tests
 * This class provides simplified methods to run Lighthouse on the current Selenium session
 */
public class LighthouseHelper {

    /**
     * Run a quick Lighthouse audit on the current page in the Selenium session
     * @return LighthouseMetrics with the audit results
     */
    @Step("Run Lighthouse audit on current page")
    public static LighthouseRunner.LighthouseMetrics auditCurrentPage() throws Exception {
        WebDriver driver = DriverManager.getWebDriver();
        String currentUrl = driver.getCurrentUrl();
        int debuggingPort = DriverManager.getDebuggingPort();
        
        return LighthouseRunner.runLighthouseOnSeleniumSession(currentUrl, debuggingPort);
    }

    /**
     * Run Lighthouse audit with specific options on current page
     * @param options Map of Lighthouse options (e.g., emulated-form-factor, categories)
     * @return LighthouseMetrics with the audit results
     */
    @Step("Run Lighthouse audit with custom options")
    public static LighthouseRunner.LighthouseMetrics auditCurrentPage(Map<String, String> options) throws Exception {
        WebDriver driver = DriverManager.getWebDriver();
        String currentUrl = driver.getCurrentUrl();
        int debuggingPort = DriverManager.getDebuggingPort();
        
        return LighthouseRunner.runLighthouseOnSeleniumSession(currentUrl, debuggingPort, options);
    }

    /**
     * Run a mobile-focused Lighthouse audit on the current page
     * @return LighthouseMetrics with mobile audit results
     */
    @Step("Run mobile Lighthouse audit")
    public static LighthouseRunner.LighthouseMetrics auditCurrentPageMobile() throws Exception {
        Map<String, String> mobileOptions = new HashMap<>();
        mobileOptions.put("emulated-form-factor", "mobile");
        mobileOptions.put("throttling-method", "simulate");
        
        return auditCurrentPage(mobileOptions);
    }

    /**
     * Run a desktop-focused Lighthouse audit on the current page
     * @return LighthouseMetrics with desktop audit results
     */
    @Step("Run desktop Lighthouse audit")
    public static LighthouseRunner.LighthouseMetrics auditCurrentPageDesktop() throws Exception {
        Map<String, String> desktopOptions = new HashMap<>();
        desktopOptions.put("emulated-form-factor", "desktop");
        desktopOptions.put("throttling-method", "simulate");
        
        return auditCurrentPage(desktopOptions);
    }

    /**
     * Run a performance-only Lighthouse audit (faster execution)
     * @return LighthouseMetrics with performance-focused results
     */
    @Step("Run performance-only Lighthouse audit")
    public static LighthouseRunner.LighthouseMetrics auditPerformanceOnly() throws Exception {
        Map<String, String> performanceOptions = new HashMap<>();
        performanceOptions.put("only-categories", "performance");
        
        return auditCurrentPage(performanceOptions);
    }

    /**
     * Run Lighthouse audit and automatically attach results to Allure report
     * @param testName Name of the test for report attachment
     * @return LighthouseMetrics with the audit results
     */
    @Step("Run Lighthouse audit and attach to Allure: {testName}")
    public static LighthouseRunner.LighthouseMetrics auditAndAttachToAllure(String testName) throws Exception {
        LighthouseRunner.LighthouseMetrics metrics = auditCurrentPage();
        
        WebDriver driver = DriverManager.getWebDriver();
        String currentUrl = driver.getCurrentUrl();
        
        // Attach all reports to Allure
        LighthouseRunner.attachAllReportsToAllure(metrics, currentUrl, testName);
        
        return metrics;
    }

    /**
     * Validate that performance scores meet minimum thresholds
     * @param metrics The Lighthouse metrics to validate
     * @param minPerformance Minimum performance score (0-100)
     * @param minAccessibility Minimum accessibility score (0-100)
     * @param minBestPractices Minimum best practices score (0-100)
     * @param minSeo Minimum SEO score (0-100)
     * @throws AssertionError if any score is below the threshold
     */
    @Step("Validate Lighthouse scores meet thresholds")
    public static void validateScores(LighthouseRunner.LighthouseMetrics metrics,
                                    double minPerformance,
                                    double minAccessibility,
                                    double minBestPractices,
                                    double minSeo) {
        
        double performanceScore = metrics.getPerformanceScore() * 100;
        double accessibilityScore = metrics.getAccessibilityScore() * 100;
        double bestPracticesScore = metrics.getBestPracticesScore() * 100;
        double seoScore = metrics.getSeoScore() * 100;
        
        StringBuilder errors = new StringBuilder();
        
        if (performanceScore < minPerformance) {
            errors.append(String.format("Performance: %.1f%% < %.1f%%; ", performanceScore, minPerformance));
        }
        
        if (accessibilityScore < minAccessibility) {
            errors.append(String.format("Accessibility: %.1f%% < %.1f%%; ", accessibilityScore, minAccessibility));
        }
        
        if (bestPracticesScore < minBestPractices) {
            errors.append(String.format("Best Practices: %.1f%% < %.1f%%; ", bestPracticesScore, minBestPractices));
        }
        
        if (seoScore < minSeo) {
            errors.append(String.format("SEO: %.1f%% < %.1f%%; ", seoScore, minSeo));
        }
        
        if (errors.length() > 0) {
            throw new AssertionError("Lighthouse scores below thresholds: " + errors.toString());
        }
        
        System.out.println("✅ All Lighthouse scores meet the required thresholds");
    }

    /**
     * Validate Core Web Vitals meet Google's recommended thresholds
     * @param metrics The Lighthouse metrics to validate
     * @throws AssertionError if any Core Web Vital is outside the recommended range
     */
    @Step("Validate Core Web Vitals")
    public static void validateCoreWebVitals(LighthouseRunner.LighthouseMetrics metrics) {
        double fcp = metrics.getFirstContentfulPaint();
        double lcp = metrics.getLargestContentfulPaint();
        double cls = metrics.getCumulativeLayoutShift();
        double tbt = metrics.getTotalBlockingTime();
        
        StringBuilder errors = new StringBuilder();
        
        // Google's recommended thresholds
        if (fcp > 1800) { // Good: ≤1.8s
            errors.append(String.format("FCP: %.0fms > 1800ms; ", fcp));
        }
        
        if (lcp > 2500) { // Good: ≤2.5s
            errors.append(String.format("LCP: %.0fms > 2500ms; ", lcp));
        }
        
        if (cls > 0.1) { // Good: ≤0.1
            errors.append(String.format("CLS: %.3f > 0.1; ", cls));
        }
        
        if (tbt > 200) { // Good: ≤200ms
            errors.append(String.format("TBT: %.0fms > 200ms; ", tbt));
        }
        
        if (errors.length() > 0) {
            throw new AssertionError("Core Web Vitals outside recommended thresholds: " + errors.toString());
        }
        
        System.out.println("✅ All Core Web Vitals meet Google's recommended thresholds");
    }

    /**
     * Simple method to audit current page and validate basic performance
     * Useful for adding to existing tests with minimal code changes
     * @param testName Name for the test report
     */
    @Step("Quick performance check: {testName}")
    public static void quickPerformanceCheck(String testName) throws Exception {
        LighthouseRunner.LighthouseMetrics metrics = auditAndAttachToAllure(testName);
        
        // Basic validation - 50% performance, 80% accessibility
        validateScores(metrics, 50.0, 80.0, 70.0, 80.0);
        
        System.out.println("✅ Quick performance check passed for: " + testName);
    }

    /**
     * Comprehensive performance audit with strict validation
     * @param testName Name for the test report
     */
    @Step("Comprehensive performance audit: {testName}")
    public static void comprehensivePerformanceAudit(String testName) throws Exception {
        LighthouseRunner.LighthouseMetrics metrics = auditAndAttachToAllure(testName);
        
        // Strict validation
        validateScores(metrics, 70.0, 90.0, 80.0, 90.0);
        validateCoreWebVitals(metrics);
        
        System.out.println("✅ Comprehensive performance audit passed for: " + testName);
    }
}