package com.choice.testing.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Random;

public class HotelSearchResultsPage extends BasePage {
    
    @FindBy(css = "li.search-results-map-card")
    private List<WebElement> hotelResults;
    
    @FindBy(css = ".results-count, .search-results-header")
    private WebElement resultsHeader;
    
    @FindBy(css = ".no-results, .no-hotels-found")
    private WebElement noResultsMessage;
    
    @FindBy(css = "h1, .page-title")
    private WebElement pageTitle;
    
    @FindBy(css = "li.search-results-map-card button.button-overlay")
    private List<WebElement> hotelCardButtons;
    
    @FindBy(css = ".choice-button.primary_cta[data-track-id*='CheckAvailability']")
    private WebElement seeAvailabilityButton;
    
    @FindBy(css = "div.map-flyout")
    private WebElement mapFlyout;
    
    public boolean hasSearchResults() {
        try {
            // Wait for either results or no results message
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfAllElements(hotelResults),
                ExpectedConditions.visibilityOf(noResultsMessage)
            ));
            
            return hotelResults.size() > 0;
        } catch (Exception e) {
            return false;
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
    
    
    public void clickRandomHotelCard() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(hotelCardButtons));
            if (hotelCardButtons.isEmpty()) {
                throw new RuntimeException("No hotel cards available to click");
            }
            
            Random random = new Random();
            int randomIndex = random.nextInt(hotelCardButtons.size());
            WebElement selectedCard = hotelCardButtons.get(randomIndex);
            
            System.out.println("🎲 Clicking random hotel card at index: " + (randomIndex + 1) + " of " + hotelCardButtons.size());
            clickElement(selectedCard);
            System.out.println("🏨 Successfully clicked random hotel card - map flyout should appear");
        } catch (Exception e) {
            throw new RuntimeException("Failed to click random hotel card: " + e.getMessage(), e);
        }
    }
    
    public void clickSeeAvailabilityButton() {
        try {
            // Wait for map flyout to appear first
            wait.until(ExpectedConditions.visibilityOf(mapFlyout));
            System.out.println("🗺️ Map flyout appeared");
            
            // Now click the See Availability button
            wait.until(ExpectedConditions.elementToBeClickable(seeAvailabilityButton));
            clickElement(seeAvailabilityButton);
            System.out.println("🔗 Clicked 'See Availability' button - navigating to hotel details");
        } catch (Exception e) {
            throw new RuntimeException("Failed to click See Availability button: " + e.getMessage(), e);
        }
    }
    
    public void selectRandomHotelAndViewDetails() {
        // Step 1: Click random hotel card (opens map flyout)
        clickRandomHotelCard();
        
        // Step 2: Click See Availability button (goes to hotel details)
        clickSeeAvailabilityButton();
        
        System.out.println("✅ Completed 2-step hotel selection process");
    }
    
    public String getRandomHotelInfo() {
        try {
            WebElement randomCard = selectRandomHotelCard();
            return randomCard.getText();
        } catch (Exception e) {
            return "Could not retrieve hotel information";
        }
    }
    
    private boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
