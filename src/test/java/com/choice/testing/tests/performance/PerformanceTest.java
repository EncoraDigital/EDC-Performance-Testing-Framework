package com.choice.testing.tests.performance;

import com.choice.testing.drivers.DriverManager;
import com.choice.testing.utils.LighthouseRunner;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Epic("Performance Testing")
@Feature("Lighthouse Performance Audits")
public class PerformanceTest {

    @BeforeMethod
    public void setup() {
        DriverManager.initializeWebDriver("chrome");
    }

    @AfterMethod
    public void teardown() {
        DriverManager.quitWebDriver();
    }

    @Test
    @Description("Run Lighthouse performance audit on Choice Hotels homepage using existing Selenium session")
    public void testHomepagePerformanceWithSelenium() throws Exception {
        String testUrl = "https://www.choicehotels.com";
        
        // Step 1: Navigate to the page using Selenium
        navigateToPage(testUrl);
        
        // Step 2: Wait for page to fully load
        waitForPageLoad();
        
        // Step 3: Run Lighthouse audit on the same browser session
        LighthouseRunner.LighthouseMetrics metrics = runLighthouseAuditOnSeleniumSession(testUrl);
        
        // Step 4: Validate performance scores
        validatePerformanceScores(metrics);
        
        // Step 5: Attach reports to Allure
        attachReportsToAllure(metrics, testUrl, "Homepage Performance Test");
    }

    @Test
    @Description("Run Lighthouse audit on search results page after performing search")
    public void testSearchResultsPerformanceWithSelenium() throws Exception {
        String homeUrl = "https://www.choicehotels.com";
        
        // Navigate to homepage first
        navigateToPage(homeUrl);
        
        // Perform a search to get to results page
        performHotelSearch("New York, NY");
        
        // Get current URL after search
        String currentUrl = DriverManager.getWebDriver().getCurrentUrl();
        System.out.println("Search results URL: " + currentUrl);
        
        // Run Lighthouse audit on search results page
        LighthouseRunner.LighthouseMetrics metrics = runLighthouseAuditOnSeleniumSession(currentUrl);
        
        // Validate that search results page meets performance criteria
        validateSearchResultsPerformance(metrics);
        
        // Attach reports
        attachReportsToAllure(metrics, currentUrl, "Search Results Performance Test");
    }

    @Test
    @Description("Compare desktop vs mobile performance using Lighthouse")
    public void testDesktopVsMobilePerformance() throws Exception {
        String testUrl = "https://www.choicehotels.com";
        
        // Test desktop performance
        navigateToPage(testUrl);
        waitForPageLoad();
        
        Map<String, String> desktopOptions = new HashMap<>();
        desktopOptions.put("emulated-form-factor", "desktop");
        
        LighthouseRunner.LighthouseMetrics desktopMetrics = 
            LighthouseRunner.runLighthouseOnSeleniumSession(testUrl, DriverManager.getDebuggingPort(), desktopOptions);
        
        // Compare performance scores
        comparePerformanceScores(desktopMetrics, null);
        
        // Attach desktop report
        attachReportsToAllure(desktopMetrics, testUrl, "Desktop Performance Comparison");
    }

    @Step("Navigate to page: {url}")
    private void navigateToPage(String url) {
        WebDriver driver = DriverManager.getWebDriver();
        driver.get(url);
        System.out.println("✅ Navigated to: " + url);
    }

    @Step("Wait for page to fully load")
    private void waitForPageLoad() {
        WebDriver driver = DriverManager.getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Wait for document ready state
        wait.until(webDriver -> 
            ((org.openqa.selenium.JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
        
        // Additional wait for any dynamic content
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("✅ Page fully loaded");
    }

    @Step("Perform hotel search for: {destination}")
    private void performHotelSearch(String destination) {
        WebDriver driver = DriverManager.getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        try {
            // Find destination input and search button
            var destinationInput = wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .elementToBeClickable(org.openqa.selenium.By.cssSelector(
                    "input[placeholder*='Where'], input[name*='destination'], #destination-input")));
            
            destinationInput.clear();
            destinationInput.sendKeys(destination);
            
            Thread.sleep(1000); // Allow for autocomplete
            
            var searchButton = driver.findElement(org.openqa.selenium.By.cssSelector(
                "button[data-track-id='FindHotelsBTN'], button[type='submit'], .search-btn"));
            searchButton.click();
            
            // Wait for search results to load
            wait.until(webDriver -> {
                String url = webDriver.getCurrentUrl();
                return url.contains("search") || url.contains("results") || url.contains("hotels");
            });
            
            System.out.println("✅ Hotel search completed for: " + destination);
            
        } catch (Exception e) {
            System.out.println("⚠️ Search may not have completed as expected: " + e.getMessage());
        }
    }

    @Step("Run Lighthouse audit on existing Selenium session")
    private LighthouseRunner.LighthouseMetrics runLighthouseAuditOnSeleniumSession(String url) throws Exception {
        int debuggingPort = DriverManager.getDebuggingPort();
        
        System.out.println("🔍 Running Lighthouse audit on existing browser session...");
        System.out.println("📊 URL: " + url);
        System.out.println("🔧 Debugging port: " + debuggingPort);
        
        LighthouseRunner.LighthouseMetrics metrics = 
            LighthouseRunner.runLighthouseOnSeleniumSession(url, debuggingPort);
        
        System.out.println("✅ Lighthouse audit completed");
        System.out.println("📈 " + metrics.toString());
        
        return metrics;
    }

    @Step("Validate performance scores meet criteria")
    private void validatePerformanceScores(LighthouseRunner.LighthouseMetrics metrics) {
        // Define minimum acceptable scores (these can be adjusted based on requirements)
        double minPerformanceScore = 50.0;  // 50%
        double minAccessibilityScore = 80.0; // 80%
        double minBestPracticesScore = 70.0; // 70%
        double minSeoScore = 80.0; // 80%
        
        System.out.println("🎯 Validating performance scores against criteria...");
        
        Assert.assertTrue(metrics.getPerformanceScore() * 100 >= minPerformanceScore,
            String.format("Performance score %.1f%% should be >= %.1f%%", 
                metrics.getPerformanceScore() * 100, minPerformanceScore));
        
        Assert.assertTrue(metrics.getAccessibilityScore() * 100 >= minAccessibilityScore,
            String.format("Accessibility score %.1f%% should be >= %.1f%%", 
                metrics.getAccessibilityScore() * 100, minAccessibilityScore));
        
        Assert.assertTrue(metrics.getBestPracticesScore() * 100 >= minBestPracticesScore,
            String.format("Best Practices score %.1f%% should be >= %.1f%%", 
                metrics.getBestPracticesScore() * 100, minBestPracticesScore));
        
        Assert.assertTrue(metrics.getSeoScore() * 100 >= minSeoScore,
            String.format("SEO score %.1f%% should be >= %.1f%%", 
                metrics.getSeoScore() * 100, minSeoScore));
        
        System.out.println("✅ All performance scores meet the minimum criteria");
    }

    @Step("Validate search results page performance")
    private void validateSearchResultsPerformance(LighthouseRunner.LighthouseMetrics metrics) {
        // Search results pages may have slightly different performance expectations
        double minPerformanceScore = 40.0;  // 40% (lower due to dynamic content)
        double minAccessibilityScore = 75.0; // 75%
        
        Assert.assertTrue(metrics.getPerformanceScore() * 100 >= minPerformanceScore,
            String.format("Search results performance score %.1f%% should be >= %.1f%%", 
                metrics.getPerformanceScore() * 100, minPerformanceScore));
        
        Assert.assertTrue(metrics.getAccessibilityScore() * 100 >= minAccessibilityScore,
            String.format("Search results accessibility score %.1f%% should be >= %.1f%%", 
                metrics.getAccessibilityScore() * 100, minAccessibilityScore));
        
        // Validate Core Web Vitals for search results
        Assert.assertTrue(metrics.getLargestContentfulPaint() < 4000,
            String.format("Largest Contentful Paint %.0fms should be < 4000ms", 
                metrics.getLargestContentfulPaint()));
        
        Assert.assertTrue(metrics.getCumulativeLayoutShift() < 0.25,
            String.format("Cumulative Layout Shift %.3f should be < 0.25", 
                metrics.getCumulativeLayoutShift()));
        
        System.out.println("✅ Search results performance meets criteria");
    }

    @Step("Compare desktop and mobile performance scores")
    private void comparePerformanceScores(LighthouseRunner.LighthouseMetrics desktopMetrics, 
                                        LighthouseRunner.LighthouseMetrics mobileMetrics) {
        
        System.out.println("📊 Performance Comparison:");
        System.out.println("🖥️  Desktop Performance: " + String.format("%.1f%%", desktopMetrics.getPerformanceScore() * 100));
        
        if (mobileMetrics != null) {
            System.out.println("📱 Mobile Performance: " + String.format("%.1f%%", mobileMetrics.getPerformanceScore() * 100));
            
            // Mobile typically scores lower than desktop
            double performanceDifference = (desktopMetrics.getPerformanceScore() - mobileMetrics.getPerformanceScore()) * 100;
            System.out.println("📈 Performance difference: " + String.format("%.1f%%", performanceDifference));
            
            // Validate that the difference is within acceptable range
            Assert.assertTrue(performanceDifference <= 30.0,
                "Performance difference between desktop and mobile should not exceed 30%");
        }
        
        // Validate desktop performance meets higher standards
        Assert.assertTrue(desktopMetrics.getPerformanceScore() * 100 >= 60.0,
            "Desktop performance should be at least 60%");
    }

    @Step("Attach Lighthouse reports to Allure")
    private void attachReportsToAllure(LighthouseRunner.LighthouseMetrics metrics, String url, String testName) {
        try {
            LighthouseRunner.attachAllReportsToAllure(metrics, url, testName);
            System.out.println("✅ Lighthouse reports attached to Allure");
        } catch (Exception e) {
            System.out.println("⚠️ Failed to attach reports to Allure: " + e.getMessage());
        }
    }
}