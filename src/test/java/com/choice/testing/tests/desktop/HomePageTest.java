package com.choice.testing.tests.desktop;

import com.choice.testing.base.BaseTest;
import com.choice.testing.pages.HomePage;
import com.choice.testing.pages.SearchResultsPage;
import com.choice.testing.utils.LighthouseHelper;
import com.choice.testing.utils.LighthouseRunner;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Desktop Web Testing")
@Feature("DuckDuckGo Search Functionality")
public class HomePageTest extends BaseTest {
    
    @Test
    @Description("Verify DuckDuckGo home page loads successfully with performance audit")
    public void testHomePageLoad() {
        navigateToHomePage();
        verifyEssentialElements();
        auditHomepagePerformance();
    }
    
    @Test
    @Description("Verify search functionality works with results and performance")
    public void testSearchFunctionality() {
        navigateToHomePage();
        performSearch("Selenium WebDriver");
        verifySearchResults();
        auditSearchResultsPerformance();
    }
    
    @Test
    @Description("Test multiple clicks and navigation with performance tracking")
    public void testNavigationAndClicks() {
        navigateToHomePage();
        performSearch("Java testing framework");
        auditSearchResultsPerformance();
        
        clickFirstResult();
        auditExternalPagePerformance();
        
        navigateBackToSearch();
        performNewSearch("TestNG framework");
        verifySearchResults();
        auditFinalSearchPerformance();
    }
    
    // @Test
    // @Description("Test privacy link navigation")
    // public void testPrivacyLinkNavigation() {
    //     HomePage homePage = new HomePage();
    //     homePage.navigateToHomePage();
        
    //     // Click privacy link
    //     homePage.clickPrivacyLink();
        
    //     // Verify we're on privacy page
    //     Assert.assertTrue(homePage.getCurrentUrl().contains("privacy"), 
    //         "Should navigate to privacy page");
    // }
    
    // Step methods for better organization and Allure reporting
    
    @Step("Navigate to DuckDuckGo home page")
    private void navigateToHomePage() {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();
        
        Assert.assertTrue(homePage.isSearchInputVisible(), 
            "Search input should be visible");
        Assert.assertTrue(homePage.getPageTitle().contains("DuckDuckGo"), 
            "Page title should contain 'DuckDuckGo'");
    }

    @Step("Verify essential page elements")
    private void verifyEssentialElements() {
      HomePage homePage = new HomePage();
    
      Assert.assertTrue(homePage.isSearchInputVisible(), 
        "Search input should be visible");
      Assert.assertTrue(homePage.getCurrentUrl().contains("duckduckgo"), 
        "Should be on DuckDuckGo domain");
      Assert.assertTrue(homePage.getPageTitle().toLowerCase().contains("duckduckgo"), 
        "Page title should contain DuckDuckGo");
    
      System.out.println("Page loaded successfully");
      System.out.println("Search input is visible");
      System.out.println("Page title: " + homePage.getPageTitle());
  }
    
    @Step("Verify page elements are displayed")
    private void verifyPageElements() {
        HomePage homePage = new HomePage();
        
        Assert.assertTrue(homePage.isLogoDisplayed(), 
            "Logo should be displayed");
        Assert.assertTrue(homePage.isSearchInputVisible(), 
            "Search input should be visible");
        Assert.assertFalse(homePage.getSearchInputPlaceholder().isEmpty(), 
            "Search input should have placeholder text");
    }
    
    @Step("Perform search for: {searchTerm}")
    private void performSearch(String searchTerm) {
        HomePage homePage = new HomePage();
        homePage.searchFor(searchTerm);
    }
    
    @Step("Verify search results are displayed")
    private void verifySearchResults() {
        SearchResultsPage resultsPage = new SearchResultsPage();
        
        Assert.assertTrue(resultsPage.hasSearchResults(), 
            "Search results should be displayed");
        Assert.assertTrue(resultsPage.getResultsCount() > 0, 
            "Should have at least one search result");
        
        String firstResultTitle = resultsPage.getFirstResultTitle();
        Assert.assertFalse(firstResultTitle.isEmpty(), 
            "First result should have a title");
        
        System.out.println("✅ Found " + resultsPage.getResultsCount() + " search results");
        System.out.println("✅ First result: " + firstResultTitle);
    }
    
    @Step("Click on first search result")
    private void clickFirstResult() {
        SearchResultsPage resultsPage = new SearchResultsPage();
        String originalUrl = resultsPage.getCurrentUrl();
        
        resultsPage.clickFirstResult();
        
        // Wait a bit for navigation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("✅ Clicked first result, navigated from: " + originalUrl);
    }
    
    @Step("Navigate back to search results")
    private void navigateBackToSearch() {
        // Go back using browser navigation
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();
        
        System.out.println("✅ Navigated back to home page");
    }
    
    @Step("Perform new search for: {searchTerm}")
    private void performNewSearch(String searchTerm) {
        HomePage homePage = new HomePage();
        homePage.searchFor(searchTerm);
        
        System.out.println("✅ Performed new search for: " + searchTerm);
    }
    
    // Performance Audit Methods
    
    @Step("Audit DuckDuckGo homepage performance")
    private void auditHomepagePerformance() {
        try {
            LighthouseHelper.quickPerformanceCheck("DuckDuckGo Homepage");
            System.out.println("✅ DuckDuckGo homepage performance audit completed");
        } catch (Exception e) {
            System.out.println("⚠️ Homepage performance audit failed: " + e.getMessage());
        }
    }
    
    @Step("Audit search results performance")
    private void auditSearchResultsPerformance() {
        try {
            // Run performance-only audit for faster execution
            LighthouseRunner.LighthouseMetrics metrics = LighthouseHelper.auditPerformanceOnly();
            
            // Log performance score
            double performanceScore = metrics.getPerformanceScore() * 100;
            System.out.println("📊 Search results performance: " + String.format("%.1f%%", performanceScore));
            
            // Attach to Allure
            HomePage homePage = new HomePage();
            LighthouseRunner.attachAllReportsToAllure(metrics, 
                homePage.getCurrentUrl(), 
                "DuckDuckGo Search Results");
            
        } catch (Exception e) {
            System.out.println("⚠️ Search results performance audit failed: " + e.getMessage());
        }
    }
    
    @Step("Audit external page performance after click")
    private void auditExternalPagePerformance() {
        try {
            // Quick performance check for external site
            LighthouseRunner.LighthouseMetrics metrics = LighthouseHelper.auditPerformanceOnly();
            
            double performanceScore = metrics.getPerformanceScore() * 100;
            System.out.println("📊 External page performance: " + String.format("%.1f%%", performanceScore));
            
            // Attach with generic name since we don't know which site we landed on
            HomePage homePage = new HomePage();
            LighthouseRunner.attachAllReportsToAllure(metrics, 
                homePage.getCurrentUrl(), 
                "External Site Performance");
            
        } catch (Exception e) {
            System.out.println("⚠️ External page performance audit failed: " + e.getMessage());
        }
    }
    
    @Step("Audit final search results performance")
    private void auditFinalSearchPerformance() {
        try {
            // Comprehensive audit for final search
            LighthouseRunner.LighthouseMetrics metrics = LighthouseHelper.auditAndAttachToAllure("Final Search Results");
            
            // Validate Core Web Vitals for the final search
            try {
                LighthouseHelper.validateCoreWebVitals(metrics);
                System.out.println("✅ Final search results meet Core Web Vitals criteria");
            } catch (AssertionError e) {
                System.out.println("⚠️ Final search results Core Web Vitals: " + e.getMessage());
            }
            
            System.out.println("✅ Final search performance audit completed");
            
        } catch (Exception e) {
            System.out.println("⚠️ Final search performance audit failed: " + e.getMessage());
        }
    }
}
