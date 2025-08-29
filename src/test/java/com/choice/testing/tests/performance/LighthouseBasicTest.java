package com.choice.testing.tests.performance;

import com.choice.testing.utils.LighthouseRunner;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Performance Testing")
@Feature("Lighthouse Integration")
public class LighthouseBasicTest {
    
    @Test
    @Story("Basic Lighthouse Audit")
    @Description("Performs a basic Lighthouse audit and integrates results with Allure reporting")
    @Severity(SeverityLevel.NORMAL)
    public void testLighthouseBasicIntegration() throws Exception {
        String testUrl = "https://www.choicehotels.com";
        String testName = "Basic Lighthouse Integration Test";
        
        Allure.step("Starting Lighthouse audit for: " + testUrl, () -> {
            System.out.println("Testing Lighthouse integration with: " + testUrl);
        });
        
        LighthouseRunner.LighthouseMetrics metrics = LighthouseRunner.runLighthouseAudit(testUrl);
        
        // Attach all Lighthouse reports to Allure
        LighthouseRunner.attachAllReportsToAllure(metrics, testUrl, testName);
        
        Allure.step("Validating Lighthouse results", () -> {
            Assert.assertNotNull(metrics, "Metrics should not be null");
            Assert.assertTrue(metrics.getPerformanceScore() >= 0, "Performance score should be >= 0");
            Assert.assertTrue(metrics.getAccessibilityScore() >= 0, "Accessibility score should be >= 0");
            Assert.assertTrue(metrics.getBestPracticesScore() >= 0, "Best practices score should be >= 0");
            Assert.assertTrue(metrics.getSeoScore() >= 0, "SEO score should be >= 0");
        });
        
        System.out.println("Lighthouse audit completed successfully!");
        System.out.println("Results: " + metrics.toString());
        System.out.println("HTML Report: " + metrics.getReportPath());
    }

    @Test
    @Story("Desktop Performance Audit")
    @Description("Performs a desktop-specific Lighthouse audit with Allure integration")
    @Severity(SeverityLevel.NORMAL)
    public void testLighthouseDesktopAudit() throws Exception {
        String testUrl = "https://www.choicehotels.com";
        String testName = "Desktop Performance Audit";
        
        Allure.step("Starting Desktop Lighthouse audit for: " + testUrl, () -> {
            System.out.println("Testing Desktop Lighthouse audit with: " + testUrl);
        });
        
        LighthouseRunner.LighthouseMetrics metrics = LighthouseRunner.runDesktopAudit(testUrl);
        
        // Attach all Lighthouse reports to Allure
        LighthouseRunner.attachAllReportsToAllure(metrics, testUrl, testName);
        
        Allure.step("Validating Desktop audit results", () -> {
            Assert.assertNotNull(metrics, "Metrics should not be null");
            Assert.assertTrue(metrics.getPerformanceScore() >= 0, "Performance score should be >= 0");
        });
        
        System.out.println("Desktop Lighthouse audit completed successfully!");
        System.out.println("Results: " + metrics.toString());
    }

    @Test
    @Story("Mobile Performance Audit")
    @Description("Performs a mobile-specific Lighthouse audit with Allure integration")
    @Severity(SeverityLevel.NORMAL)
    public void testLighthouseMobileAudit() throws Exception {
        String testUrl = "https://www.choicehotels.com";
        String testName = "Mobile Performance Audit";
        
        Allure.step("Starting Mobile Lighthouse audit for: " + testUrl, () -> {
            System.out.println("Testing Mobile Lighthouse audit with: " + testUrl);
        });
        
        LighthouseRunner.LighthouseMetrics metrics = LighthouseRunner.runMobileAudit(testUrl);
        
        // Attach all Lighthouse reports to Allure
        LighthouseRunner.attachAllReportsToAllure(metrics, testUrl, testName);
        
        Allure.step("Validating Mobile audit results", () -> {
            Assert.assertNotNull(metrics, "Metrics should not be null");
            Assert.assertTrue(metrics.getPerformanceScore() >= 0, "Performance score should be >= 0");
        });
        
        System.out.println("Mobile Lighthouse audit completed successfully!");
        System.out.println("Results: " + metrics.toString());
    }
}
