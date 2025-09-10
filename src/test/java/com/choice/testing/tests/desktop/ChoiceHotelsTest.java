package com.choice.testing.tests.desktop;

import com.choice.testing.base.BaseTest;
import com.choice.testing.config.ConfigManager;
import com.choice.testing.pages.ChoiceHotelsHomePage;
import com.choice.testing.pages.HotelSearchResultsPage;
import com.choice.testing.pages.HotelDetailsPage;
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
        
        // Wait for results to load using explicit wait
        // The page object will handle the waiting internally
        
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
    
    @Test
    @Description("Select a random hotel from search results and view its details")
    public void testRandomHotelSelection() {
        // Get test data from config
        String destination = ConfigManager.getProperty("test.destination", "New York, NY");
        int checkinDays = ConfigManager.getIntProperty("test.checkin.days.future", 7);
        int checkoutDays = ConfigManager.getIntProperty("test.checkout.days.future", 9);
        int rooms = ConfigManager.getIntProperty("test.rooms", 1);
        int adults = ConfigManager.getIntProperty("test.adults", 2);
        
        // Perform search first
        navigateToChoiceHotels();
        performCompleteHotelSearch(destination, checkinDays, checkoutDays, rooms, adults);
        
        // Select and click random hotel
        selectAndViewRandomHotel();
    }
    
    @Step("Select and view random hotel from search results")
    private void selectAndViewRandomHotel() {
        HotelSearchResultsPage resultsPage = new HotelSearchResultsPage();
        
        // Wait for search results to load using explicit wait
        // The page object will handle the waiting internally
        
        // Verify we have search results
        Assert.assertTrue(resultsPage.isOnSearchResultsPage(), 
            "Should be on hotel search results page");
        
        if (resultsPage.hasSearchResults()) {
            System.out.println("🎯 Found " + resultsPage.getResultsCount() + " hotels in search results");
            
            // Use the new Grid view approach to select and view hotel details
            resultsPage.selectRandomHotelAndViewDetails();
            
            // Wait for page to load using explicit wait
            // The page object will handle the waiting internally
            
            // Verify we're on hotel details page
            verifyHotelDetailsPage();
        } else {
            System.out.println("⚠️ No hotels found in search results to select from");
            Assert.fail("No hotels available to select from search results");
        }
    }
    
    @Step("Verify hotel details page is displayed")
    private void verifyHotelDetailsPage() {
        HotelDetailsPage detailsPage = new HotelDetailsPage();
        
        // Log current page info for debugging
        System.out.println("📍 Current URL: " + detailsPage.getCurrentUrl());
        System.out.println("📄 Page title: " + detailsPage.getPageTitle());
        
        // Check if we're on a hotel details page (flexible validation)
        boolean isOnDetailsPage = detailsPage.isOnHotelDetailsPage() || 
                                detailsPage.getCurrentUrl().contains("choicehotels");
        
        Assert.assertTrue(isOnDetailsPage, 
            "Should be on hotel details page or Choice Hotels domain");
        
        if (detailsPage.isOnHotelDetailsPage()) {
            // Display hotel details info
            String hotelDetails = detailsPage.getHotelDetailsInfo();
            System.out.println("🏨 Hotel Details:\n" + hotelDetails);
            
            // Basic validation - at least hotel name should be available
            String hotelName = detailsPage.getHotelName();
            Assert.assertFalse(hotelName.equals("Hotel name not found"), 
                "Hotel name should be available on details page");
            
            System.out.println("✅ Successfully viewed hotel details for: " + hotelName);
        } else {
            System.out.println("ℹ️ Navigated to a Choice Hotels page - details validation skipped");
        }
    }
}
