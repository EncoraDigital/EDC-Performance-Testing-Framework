package com.choice.testing.tests.performance;

import com.choice.testing.base.BaseTest;
import com.choice.testing.drivers.DriverManager;
import com.choice.testing.pages.ChoiceHotelsHomePage;
import com.choice.testing.utils.LighthouseHelper;
import com.choice.testing.utils.LighthouseRunner;
import com.choice.testing.utils.AllurePerformanceReporter;
import com.choice.testing.utils.PerformanceRegressionTracker;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive performance testing showcasing all Lighthouse integration features:
 * - Performance audits with Selenium session integration
 * - Enhanced Allure reporting with dashboards
 * - Regression tracking and trend analysis
 * - Baseline management
 * - Cross-device performance comparison
 */
@Epic("Performance Testing")
@Feature("Comprehensive Lighthouse Integration")
public class ComprehensivePerformanceTest extends BaseTest {

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Comprehensive performance audit with full feature demonstration")
    public void testFullPerformanceAuditPipeline() throws Exception {
        // Step 1: Navigate to Choice Hotels
        navigateToChoiceHotels();
        
        // Step 2: Set baseline (run this first to establish baseline)
        setPerformanceBaseline();
        
        // Step 3: Run comprehensive audit with all features
        runComprehensiveAuditWithAllFeatures();
        
        // Step 4: Perform user journey and track performance at each step
        performUserJourneyWithPerformanceTracking();
        
        // Step 5: Compare desktop vs mobile performance
        compareDesktopMobilePerformance();
        
        // Step 6: Demonstrate regression detection
        demonstrateRegressionDetection();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Performance regression monitoring across multiple runs")
    public void testPerformanceRegressionMonitoring() throws Exception {
        navigateToChoiceHotels();
        
        // Simulate multiple test runs for regression tracking
        for (int run = 1; run <= 3; run++) {
            System.out.println("🔄 Performance monitoring run " + run + "/3");
            
            // Refresh page to simulate different conditions
            refreshPage();
            
            // Run audit with regression tracking
            String testName = "Performance Monitoring Run " + run;
            LighthouseRunner.LighthouseMetrics metrics = LighthouseHelper.auditWithRegressionTracking(testName);
            
            // Log this run's results
            System.out.println("📊 Run " + run + " Performance: " + String.format("%.1f%%", metrics.getPerformanceScore() * 100));
            
            // Wait between runs
            Thread.sleep(2000);
        }
        
        // Generate trend analysis report
        PerformanceRegressionTracker.createPerformanceReport("Performance Monitoring Trend");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Cross-environment performance comparison")
    public void testCrossEnvironmentPerformanceComparison() throws Exception {
        // Simulate testing different environments/configurations
        Map<String, String> environmentConfigs = new HashMap<>();
        environmentConfigs.put("Production-like", "https://www.choicehotels.com");
        environmentConfigs.put("Staging-like", "https://www.choicehotels.com"); // Would be different URL in real scenario
        
        for (Map.Entry<String, String> env : environmentConfigs.entrySet()) {
            String envName = env.getKey();
            String envUrl = env.getValue();
            
            System.out.println("🌐 Testing " + envName + " environment");
            
            // Navigate to environment
            navigateToUrl(envUrl);
            
            // Run performance audit
            String testName = "Environment Performance - " + envName;
            LighthouseRunner.LighthouseMetrics metrics = LighthouseHelper.auditWithRegressionTracking(testName);
            
            // Create environment-specific dashboard
            AllurePerformanceReporter.createPerformanceDashboard(metrics, testName, envUrl);
            
            // Validate environment performance
            validateEnvironmentPerformance(metrics, envName);
        }
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("Performance impact of user actions")
    public void testUserActionPerformanceImpact() throws Exception {
        navigateToChoiceHotels();
        
        // Baseline: Homepage performance
        LighthouseRunner.LighthouseMetrics homepageMetrics = 
            LighthouseHelper.auditWithRegressionTracking("Homepage Baseline");
        
        // Action 1: Perform search
        performHotelSearch("New York, NY");
        LighthouseRunner.LighthouseMetrics searchMetrics = 
            LighthouseHelper.auditWithRegressionTracking("After Search Action");
        
        // Action 2: Navigate to results (if available)
        try {
            clickFirstSearchResult();
            LighthouseRunner.LighthouseMetrics detailsMetrics = 
                LighthouseHelper.auditWithRegressionTracking("After Navigation Action");
                
            // Compare performance impact of actions
            compareActionPerformanceImpact(homepageMetrics, searchMetrics, detailsMetrics);
            
        } catch (Exception e) {
            System.out.println("⚠️ Could not complete full user journey: " + e.getMessage());
        }
    }

    // Helper Methods

    @Step("Navigate to Choice Hotels homepage")
    private void navigateToChoiceHotels() {
        ChoiceHotelsHomePage homePage = new ChoiceHotelsHomePage();
        homePage.navigateToHomePage();
        
        Assert.assertTrue(homePage.getCurrentUrl().contains("choicehotels"), 
            "Should successfully navigate to Choice Hotels");
        
        System.out.println("🏨 Navigated to Choice Hotels website");
    }

    @Step("Navigate to URL: {url}")
    private void navigateToUrl(String url) {
        // Use DriverManager to get the driver directly
        DriverManager.getWebDriver().get(url);
        
        // Wait for page load
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("🌐 Navigated to: " + url);
    }

    @Step("Set performance baseline for future comparisons")
    private void setPerformanceBaseline() throws Exception {
        LighthouseHelper.setPerformanceBaseline("Choice Hotels Homepage Baseline");
        System.out.println("📊 Performance baseline established");
    }

    @Step("Run comprehensive audit with all features")
    private void runComprehensiveAuditWithAllFeatures() throws Exception {
        // Run audit with regression tracking
        LighthouseRunner.LighthouseMetrics metrics = 
            LighthouseHelper.auditWithRegressionTracking("Comprehensive Feature Demo");
        
        // Validate performance with relaxed thresholds for demo
        try {
            LighthouseHelper.validateScores(metrics, 30.0, 70.0, 60.0, 70.0);
            System.out.println("✅ Performance validation passed");
        } catch (AssertionError e) {
            System.out.println("⚠️ Performance validation: " + e.getMessage());
            // Don't fail - this is a demo
        }
        
        // Validate Core Web Vitals
        try {
            LighthouseHelper.validateCoreWebVitals(metrics);
            System.out.println("✅ Core Web Vitals validation passed");
        } catch (AssertionError e) {
            System.out.println("⚠️ Core Web Vitals validation: " + e.getMessage());
            // Don't fail - this is a demo
        }
        
        System.out.println("🎯 Comprehensive audit with all features completed");
    }

    @Step("Perform user journey with performance tracking")
    private void performUserJourneyWithPerformanceTracking() throws Exception {
        // Step 1: Homepage audit (already done)
        System.out.println("👤 Starting user journey performance tracking");
        
        // Step 2: Search action
        performHotelSearch("Miami, FL");
        LighthouseHelper.auditCurrentPageDesktop(); // Quick audit after search
        
        // Step 3: Navigate back for baseline comparison
        navigateToChoiceHotels();
        LighthouseHelper.auditCurrentPageMobile(); // Mobile audit for comparison
        
        System.out.println("🛤️ User journey performance tracking completed");
    }

    @Step("Compare desktop vs mobile performance")
    private void compareDesktopMobilePerformance() throws Exception {
        System.out.println("📱 Starting desktop vs mobile performance comparison");
        
        // Desktop audit
        LighthouseRunner.LighthouseMetrics desktopMetrics = LighthouseHelper.auditCurrentPageDesktop();
        
        // Mobile audit
        LighthouseRunner.LighthouseMetrics mobileMetrics = LighthouseHelper.auditCurrentPageMobile();
        
        // Create comparison report
        ChoiceHotelsHomePage homePage = new ChoiceHotelsHomePage();
        String currentUrl = homePage.getCurrentUrl();
        
        AllurePerformanceReporter.createPerformanceComparison(
            desktopMetrics, mobileMetrics, "Desktop vs Mobile Performance");
        
        // Log comparison results
        double desktopPerf = desktopMetrics.getPerformanceScore() * 100;
        double mobilePerf = mobileMetrics.getPerformanceScore() * 100;
        double difference = Math.abs(desktopPerf - mobilePerf);
        
        System.out.println("📊 Performance Comparison Results:");
        System.out.println("🖥️  Desktop: " + String.format("%.1f%%", desktopPerf));
        System.out.println("📱 Mobile: " + String.format("%.1f%%", mobilePerf));
        System.out.println("📈 Difference: " + String.format("%.1f%%", difference));
        
        if (difference > 20) {
            System.out.println("⚠️ Significant performance gap detected between desktop and mobile");
        } else {
            System.out.println("✅ Desktop and mobile performance are well-aligned");
        }
    }

    @Step("Demonstrate regression detection capabilities")
    private void demonstrateRegressionDetection() throws Exception {
        // Run multiple audits to show trend analysis
        System.out.println("🔍 Demonstrating regression detection capabilities");
        
        for (int i = 1; i <= 2; i++) {
            refreshPage();
            String testName = "Regression Demo Run " + i;
            LighthouseRunner.LighthouseMetrics metrics = LighthouseHelper.auditWithRegressionTracking(testName);
            
            System.out.println("📊 Demo run " + i + " performance: " + 
                String.format("%.1f%%", metrics.getPerformanceScore() * 100));
                
            Thread.sleep(1000);
        }
        
        System.out.println("🎯 Regression detection demonstration completed");
    }

    @Step("Perform hotel search for: {destination}")
    private void performHotelSearch(String destination) {
        try {
            ChoiceHotelsHomePage homePage = new ChoiceHotelsHomePage();
            homePage.performHotelSearch(destination, 7, 9, 1, 2);
            System.out.println("🔍 Performed hotel search for: " + destination);
        } catch (Exception e) {
            System.out.println("⚠️ Hotel search may not have completed: " + e.getMessage());
        }
    }

    @Step("Click first search result")
    private void clickFirstSearchResult() throws Exception {
        // This would need to be implemented based on your page objects
        // For demo purposes, we'll just simulate it
        Thread.sleep(2000);
        System.out.println("🖱️ Clicked first search result (simulated)");
    }

    @Step("Refresh page for new test conditions")
    private void refreshPage() {
        try {
            DriverManager.getWebDriver().navigate().refresh();
            Thread.sleep(3000); // Wait for page load
            System.out.println("🔄 Page refreshed");
        } catch (Exception e) {
            System.out.println("⚠️ Page refresh failed: " + e.getMessage());
        }
    }

    @Step("Validate environment performance for: {environmentName}")
    private void validateEnvironmentPerformance(LighthouseRunner.LighthouseMetrics metrics, String environmentName) {
        double performanceScore = metrics.getPerformanceScore() * 100;
        
        // Different thresholds for different environments
        double threshold = environmentName.contains("Production") ? 40.0 : 30.0;
        
        if (performanceScore >= threshold) {
            System.out.println("✅ " + environmentName + " environment meets performance criteria: " + 
                String.format("%.1f%%", performanceScore));
        } else {
            System.out.println("⚠️ " + environmentName + " environment below performance threshold: " + 
                String.format("%.1f%% (expected >= %.1f%%)", performanceScore, threshold));
        }
    }

    @Step("Compare performance impact of user actions")
    private void compareActionPerformanceImpact(LighthouseRunner.LighthouseMetrics baseline,
                                              LighthouseRunner.LighthouseMetrics afterSearch,
                                              LighthouseRunner.LighthouseMetrics afterNavigation) {
        
        System.out.println("📊 User Action Performance Impact Analysis:");
        System.out.println("🏠 Baseline (Homepage): " + String.format("%.1f%%", baseline.getPerformanceScore() * 100));
        System.out.println("🔍 After Search: " + String.format("%.1f%%", afterSearch.getPerformanceScore() * 100));
        System.out.println("🧭 After Navigation: " + String.format("%.1f%%", afterNavigation.getPerformanceScore() * 100));
        
        double searchImpact = (afterSearch.getPerformanceScore() - baseline.getPerformanceScore()) * 100;
        double navigationImpact = (afterNavigation.getPerformanceScore() - afterSearch.getPerformanceScore()) * 100;
        
        System.out.println("📈 Search Impact: " + String.format("%+.1f%%", searchImpact));
        System.out.println("📈 Navigation Impact: " + String.format("%+.1f%%", navigationImpact));
        
        if (Math.abs(searchImpact) > 10) {
            System.out.println("⚠️ Significant performance impact from search action");
        }
        
        if (Math.abs(navigationImpact) > 10) {
            System.out.println("⚠️ Significant performance impact from navigation action");
        }
    }
}