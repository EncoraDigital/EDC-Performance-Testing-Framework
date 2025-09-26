package com.choice.testing.tests.examples;

import com.choice.testing.drivers.DriverManager;
import com.choice.testing.utils.LighthouseHelper;
import com.choice.testing.utils.LighthouseRunner;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Example test class showing different ways to integrate Lighthouse audits
 * into existing Selenium tests with minimal code changes
 */
@Epic("Performance Testing Examples")
@Feature("Lighthouse Integration")
public class LighthouseIntegrationExample {

    @BeforeMethod
    public void setup() {
        // Initialize Chrome with remote debugging enabled
        DriverManager.initializeWebDriver("chrome");
    }

    @AfterMethod
    public void teardown() {
        DriverManager.quitWebDriver();
    }

    @Test
    @Description("Example 1: Add basic performance check to existing test with one line")
    public void testWithBasicPerformanceCheck() throws Exception {
        // Your existing test code...
        navigateToChoiceHotels();
        
        // Add Lighthouse audit with just one line!
        LighthouseHelper.quickPerformanceCheck("Choice Hotels Homepage");
        
        // Continue with your existing test...
        performBasicNavigation();
    }

    @Test
    @Description("Example 2: Audit performance after each major user action")
    public void testWithPerformanceAtEachStep() throws Exception {
        // Step 1: Navigate to homepage
        navigateToChoiceHotels();
        LighthouseHelper.auditAndAttachToAllure("Homepage Load");
        
        // Step 2: Perform search
        performHotelSearch("New York, NY");
        LighthouseHelper.auditAndAttachToAllure("Search Results");
        
        // Step 3: View hotel details (if available)
        try {
            clickFirstHotelResult();
            LighthouseHelper.auditAndAttachToAllure("Hotel Details Page");
        } catch (Exception e) {
            System.out.println("Hotel details step skipped: " + e.getMessage());
        }
    }

    @Test
    @Description("Example 3: Compare mobile vs desktop performance")
    public void testMobileVsDesktopPerformance() throws Exception {
        navigateToChoiceHotels();
        
        // Audit desktop performance
        LighthouseRunner.LighthouseMetrics desktopMetrics = 
            LighthouseHelper.auditCurrentPageDesktop();
        
        // Audit mobile performance
        LighthouseRunner.LighthouseMetrics mobileMetrics = 
            LighthouseHelper.auditCurrentPageMobile();
        
        // Compare and report
        comparePerformanceMetrics(desktopMetrics, mobileMetrics);
        
        // Attach both reports
        LighthouseRunner.attachAllReportsToAllure(desktopMetrics, 
            DriverManager.getWebDriver().getCurrentUrl(), "Desktop Performance");
        LighthouseRunner.attachAllReportsToAllure(mobileMetrics, 
            DriverManager.getWebDriver().getCurrentUrl(), "Mobile Performance");
    }

    @Test
    @Description("Example 4: Performance-only audit for faster execution")
    public void testFastPerformanceCheck() throws Exception {
        navigateToChoiceHotels();
        
        // Run only performance audit (faster than full audit)
        LighthouseRunner.LighthouseMetrics metrics = 
            LighthouseHelper.auditPerformanceOnly();
        
        // Validate performance score
        if (metrics.getPerformanceScore() * 100 < 60) {
            System.out.println("⚠️ Performance score below 60%: " + 
                String.format("%.1f%%", metrics.getPerformanceScore() * 100));
        } else {
            System.out.println("✅ Performance score meets criteria: " + 
                String.format("%.1f%%", metrics.getPerformanceScore() * 100));
        }
    }

    @Test
    @Description("Example 5: Comprehensive audit with strict validation")
    public void testStrictPerformanceValidation() throws Exception {
        navigateToChoiceHotels();
        
        try {
            // This will throw an exception if performance doesn't meet strict criteria
            LighthouseHelper.comprehensivePerformanceAudit("Strict Performance Test");
            System.out.println("✅ Site meets all strict performance criteria");
        } catch (AssertionError e) {
            System.out.println("❌ Site failed strict performance criteria: " + e.getMessage());
            // You can choose to fail the test or just log the issue
            throw e;
        }
    }

    @Test
    @Description("Example 6: Custom audit with specific options")
    public void testCustomLighthouseOptions() throws Exception {
        navigateToChoiceHotels();
        
        // Create custom audit options
        java.util.Map<String, String> customOptions = new java.util.HashMap<>();
        customOptions.put("only-categories", "performance,accessibility");
        customOptions.put("throttling-method", "provided");
        customOptions.put("disable-device-emulation", "true");
        
        LighthouseRunner.LighthouseMetrics metrics = 
            LighthouseHelper.auditCurrentPage(customOptions);
        
        System.out.println("Custom audit results: " + metrics.toString());
        
        // Validate specific metrics
        LighthouseHelper.validateCoreWebVitals(metrics);
    }

    // Helper methods for the examples above

    @Step("Navigate to Choice Hotels homepage")
    private void navigateToChoiceHotels() {
        WebDriver driver = DriverManager.getWebDriver();
        driver.get("https://www.choicehotels.com");
        
        // Wait for page to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> 
            ((org.openqa.selenium.JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
        
        System.out.println("✅ Navigated to Choice Hotels homepage");
    }

    @Step("Perform basic navigation checks")
    private void performBasicNavigation() {
        WebDriver driver = DriverManager.getWebDriver();
        
        // Check that we're on the right site
        String title = driver.getTitle();
        if (title.toLowerCase().contains("choice")) {
            System.out.println("✅ Page title contains 'Choice': " + title);
        } else {
            System.out.println("⚠️ Unexpected page title: " + title);
        }
    }

    @Step("Perform hotel search for: {destination}")
    private void performHotelSearch(String destination) {
        WebDriver driver = DriverManager.getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        try {
            // Find and fill destination input
            WebElement destinationInput = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[placeholder*='Where'], input[name*='destination']")));
            
            destinationInput.clear();
            destinationInput.sendKeys(destination);
            
            Thread.sleep(1000); // Allow for autocomplete
            
            // Find and click search button
            WebElement searchButton = driver.findElement(
                By.cssSelector("button[data-track-id='FindHotelsBTN'], button[type='submit']"));
            searchButton.click();
            
            // Wait for results page
            wait.until(webDriver -> {
                String url = webDriver.getCurrentUrl();
                return url.contains("search") || url.contains("results");
            });
            
            System.out.println("✅ Hotel search completed for: " + destination);
            
        } catch (Exception e) {
            System.out.println("⚠️ Hotel search may not have completed: " + e.getMessage());
        }
    }

    @Step("Click first hotel result")
    private void clickFirstHotelResult() {
        WebDriver driver = DriverManager.getWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        try {
            WebElement firstResult = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".hotel-card, .property-card, [data-track-id*='hotel']")));
            
            firstResult.click();
            
            // Wait for hotel details page
            Thread.sleep(3000);
            
            System.out.println("✅ Clicked first hotel result");
            
        } catch (Exception e) {
            throw new RuntimeException("Could not click first hotel result", e);
        }
    }

    @Step("Compare desktop and mobile performance metrics")
    private void comparePerformanceMetrics(LighthouseRunner.LighthouseMetrics desktop, 
                                         LighthouseRunner.LighthouseMetrics mobile) {
        
        double desktopPerf = desktop.getPerformanceScore() * 100;
        double mobilePerf = mobile.getPerformanceScore() * 100;
        double difference = desktopPerf - mobilePerf;
        
        System.out.println("📊 Performance Comparison:");
        System.out.println("🖥️  Desktop: " + String.format("%.1f%%", desktopPerf));
        System.out.println("📱 Mobile:  " + String.format("%.1f%%", mobilePerf));
        System.out.println("📈 Difference: " + String.format("%.1f%%", difference));
        
        if (difference > 30) {
            System.out.println("⚠️ Large performance gap between desktop and mobile");
        } else if (difference > 0) {
            System.out.println("✅ Desktop performs better, within acceptable range");
        } else {
            System.out.println("✅ Mobile performance matches or exceeds desktop");
        }
    }
}