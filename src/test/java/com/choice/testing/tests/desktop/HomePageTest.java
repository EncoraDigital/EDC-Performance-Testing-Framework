package com.choice.testing.tests.desktop;

import com.choice.testing.base.BaseTest;
import com.choice.testing.pages.HomePage;
import com.choice.testing.pages.SearchResultsPage;
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
    @Description("Verify DuckDuckGo home page loads successfully")
    public void testHomePageLoad() {
        navigateToHomePage();
        verifyEssentialElements();
    }
    
    @Test
    @Description("Verify search functionality works with results")
    public void testSearchFunctionality() {
        navigateToHomePage();
        performSearch("Selenium WebDriver");
        verifySearchResults();
    }
    
    @Test
    @Description("Test multiple clicks and navigation")
    public void testNavigationAndClicks() {
        navigateToHomePage();
        performSearch("Java testing framework");
        clickFirstResult();
        navigateBackToSearch();
        performNewSearch("TestNG framework");
        verifySearchResults();
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
}
