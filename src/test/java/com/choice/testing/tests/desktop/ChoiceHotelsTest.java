package com.choice.testing.tests.desktop;

import com.choice.testing.base.BaseTest;
import com.choice.testing.config.ConfigManager;
import com.choice.testing.pages.ChoiceHotelsHomePage;
import com.choice.testing.pages.HotelSearchResultsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Choice Hotels Testing")
@Feature("Hotel Search Functionality")
public class ChoiceHotelsTest extends BaseTest {
    
    @Test
    @Description("Complete hotel search flow on Choice Hotels website")
    public void testHotelSearchFlow() {
        // Get test data from config
        String destination = ConfigManager.getProperty("test.destination", "New York, NY");
        int checkinDays = ConfigManager.getIntProperty("test.checkin.days.future", 7);
        int checkoutDays = ConfigManager.getIntProperty("test.checkout.days.future", 9);
        int rooms = ConfigManager.getIntProperty("test.rooms", 1);
        int adults = ConfigManager.getIntProperty("test.adults", 2);
        
        navigateToChoiceHotels();
        performCompleteHotelSearch(destination, checkinDays, checkoutDays, rooms, adults);
        verifySearchResults();
    }
    
    @Test
    @Description("Verify Choice Hotels homepage loads correctly")
    public void testChoiceHotelsHomepage() {
        ChoiceHotelsHomePage homePage = new ChoiceHotelsHomePage();
        homePage.navigateToHomePage();
        
        Assert.assertTrue(homePage.getCurrentUrl().contains("choicehotels"), 
            "Should be on Choice Hotels domain");
        Assert.assertTrue(homePage.getPageTitle().toLowerCase().contains("choice"), 
            "Page title should contain 'Choice'");
        
        System.out.println("✅ Choice Hotels homepage loaded successfully");
        System.out.println("✅ Page title: " + homePage.getPageTitle());
    }
    
    @Step("Navigate to Choice Hotels website")
    private void navigateToChoiceHotels() {
        ChoiceHotelsHomePage homePage = new ChoiceHotelsHomePage();
        homePage.navigateToHomePage();
        
        Assert.assertTrue(homePage.getCurrentUrl().contains("choicehotels"), 
            "Should successfully navigate to Choice Hotels");
        
        System.out.println("🏨 Navigated to Choice Hotels website");
    }
    
    @Step("Perform complete hotel search with: {destination}, {checkinDays} days from now, {checkoutDays} days from now, {rooms} rooms, {adults} adults")
    private void performCompleteHotelSearch(String destination, int checkinDays, int checkoutDays, int rooms, int adults) {
        ChoiceHotelsHomePage homePage = new ChoiceHotelsHomePage();
        
        System.out.println("🔍 Starting hotel search with parameters:");
        System.out.println("   Destination: " + destination);
        System.out.println("   Check-in: " + checkinDays + " days from now");
        System.out.println("   Check-out: " + checkoutDays + " days from now");
        System.out.println("   Rooms: " + rooms + ", Adults: " + adults);
        
        // Perform the search using the consolidated method
        homePage.performHotelSearch(destination, checkinDays, checkoutDays, rooms, adults);
    }
    
    @Step("Verify search results are displayed")
    private void verifySearchResults() {
        HotelSearchResultsPage resultsPage = new HotelSearchResultsPage();
        
        // Give some time for results to load
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Verify we're on a search results page
        Assert.assertTrue(resultsPage.isOnSearchResultsPage(), 
            "Should be on hotel search results page");
        
        String searchInfo = resultsPage.getSearchResultsInfo();
        System.out.println("🏨 " + searchInfo);
        
        // Log current page info for debugging
        System.out.println("📍 Current URL: " + resultsPage.getCurrentUrl());
        System.out.println("📄 Page title: " + resultsPage.getPageTitle());
        
        // The search is considered successful if we reach a results page
        // (even if no hotels are found for the specific criteria)
        Assert.assertTrue(resultsPage.getCurrentUrl().contains("choicehotels"), 
            "Should still be on Choice Hotels domain after search");
    }
}
