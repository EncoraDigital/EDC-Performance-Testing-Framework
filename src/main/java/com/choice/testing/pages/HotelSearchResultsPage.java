package com.choice.testing.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Random;

public class HotelSearchResultsPage extends BasePage {
    
    // Grid view toggle button
    @FindBy(css = "a[data-track-id='viewByGrid']")
    private WebElement gridViewButton;
    
    // Hotel cards - comprehensive locators for Choice Hotels
    @FindBy(css = "li.search-results-map-card, .hotel-card, .property-card, .hotel-result, .property-listing, [data-testid*='hotel'], [data-testid*='property'], .result-item")
    private List<WebElement> hotelResults;
    
    @FindBy(css = ".results-count, .search-results-header")
    private WebElement resultsHeader;
    
    @FindBy(css = ".no-results, .no-hotels-found")
    private WebElement noResultsMessage;
    
    @FindBy(css = "h1, .page-title")
    private WebElement pageTitle;
    
    // See Availability buttons directly in grid cards
    @FindBy(css = ".choice-button[data-track-id*='CheckAvailability'], .see-availability-button, button:contains('See Availability')")
    private List<WebElement> seeAvailabilityButtons;
    
    // Removed old map flyout locators - using Grid view approach now
    
    public boolean hasSearchResults() {
        try {
            System.out.println("🔍 Debugging: Looking for hotel results on the page");
            System.out.println("📍 Current URL: " + getCurrentUrl());
            System.out.println("📄 Page title: " + getPageTitle());
            
            // Wait for page to load
            Thread.sleep(5000);
            
            // Try to find any potential hotel-related elements
            debugPageElements();
            
            // Wait for either results or no results message
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfAllElements(hotelResults),
                    ExpectedConditions.visibilityOf(noResultsMessage)
                ));
            } catch (Exception waitException) {
                System.out.println("⚠️ Timeout waiting for results or no-results message");
            }
            
            System.out.println("🏨 Found " + hotelResults.size() + " hotel result elements");
            
            return hotelResults.size() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error in hasSearchResults: " + e.getMessage());
            return false;
        }
    }
    
    private void debugPageElements() {
        try {
            // Look for common hotel result patterns
            String[] potentialSelectors = {
                ".hotel", ".property", ".result", ".card", ".listing",
                "[data-track-id]", "[data-testid]", ".search-result", 
                ".accommodation", ".room", "li[class*='result']",
                "div[class*='hotel']", "div[class*='property']"
            };
            
            System.out.println("🔍 Scanning page for potential hotel elements:");
            for (String selector : potentialSelectors) {
                try {
                    var elements = driver.findElements(org.openqa.selenium.By.cssSelector(selector));
                    if (elements.size() > 0) {
                        System.out.println("   ✅ Found " + elements.size() + " elements matching: " + selector);
                    }
                } catch (Exception e) {
                    // Continue to next selector
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error during page debugging: " + e.getMessage());
        }
    }
    
    public int getResultsCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(hotelResults));
            return hotelResults.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public void switchToGridView() {
        try {
            System.out.println("🔄 Switching to Grid view for better hotel card access");
            wait.until(ExpectedConditions.elementToBeClickable(gridViewButton));
            clickElement(gridViewButton);
            System.out.println("✅ Successfully switched to Grid view");
            
            // Wait for grid view to load
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("⚠️ Could not switch to Grid view, continuing with current view: " + e.getMessage());
        }
    }
    
    public boolean isOnSearchResultsPage() {
        try {
            // Check if we're on a results page by URL or page elements
            return getCurrentUrl().contains("search") || 
                   getCurrentUrl().contains("results") ||
                   getCurrentUrl().contains("hotels") ||
                   isElementDisplayed(resultsHeader) ||
                   getPageTitle().toLowerCase().contains("hotels");
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getSearchResultsInfo() {
        try {
            if (hasSearchResults()) {
                return "Found " + getResultsCount() + " hotels";
            } else {
                return "No hotels found";
            }
        } catch (Exception e) {
            return "Could not determine search results";
        }
    }
    
    public WebElement selectRandomHotelCard() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(hotelResults));
            if (hotelResults.isEmpty()) {
                throw new RuntimeException("No hotel cards available to select");
            }
            
            Random random = new Random();
            int randomIndex = random.nextInt(hotelResults.size());
            WebElement selectedCard = hotelResults.get(randomIndex);
            
            System.out.println("🎲 Selected random hotel card at index: " + (randomIndex + 1) + " of " + hotelResults.size());
            return selectedCard;
        } catch (Exception e) {
            throw new RuntimeException("Failed to select random hotel card: " + e.getMessage(), e);
        }
    }
    
    
    // Removed old map flyout methods - using simplified Grid view approach
    
    public void selectRandomHotelAndViewDetails() {
        try {
            // Step 1: Switch to Grid view for easier access
            switchToGridView();
            
            // Step 2: Find available See Availability buttons
            wait.until(ExpectedConditions.visibilityOfAllElements(seeAvailabilityButtons));
            
            if (seeAvailabilityButtons.isEmpty()) {
                throw new RuntimeException("No 'See Availability' buttons found in grid view");
            }
            
            // Step 3: Select and click a random See Availability button
            Random random = new Random();
            int randomIndex = random.nextInt(seeAvailabilityButtons.size());
            WebElement randomSeeAvailabilityButton = seeAvailabilityButtons.get(randomIndex);
            
            System.out.println("🎲 Clicking random 'See Availability' button " + (randomIndex + 1) + " of " + seeAvailabilityButtons.size());
            
            wait.until(ExpectedConditions.elementToBeClickable(randomSeeAvailabilityButton));
            clickElement(randomSeeAvailabilityButton);
            
            System.out.println("✅ Successfully clicked 'See Availability' - navigating to hotel details");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to select hotel in grid view: " + e.getMessage(), e);
        }
    }
    
    public String getRandomHotelInfo() {
        try {
            WebElement randomCard = selectRandomHotelCard();
            return randomCard.getText();
        } catch (Exception e) {
            return "Could not retrieve hotel information";
        }
    }
    
    // Removed complex fallback methods - Grid view approach is much simpler and more reliable
    
    private boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
