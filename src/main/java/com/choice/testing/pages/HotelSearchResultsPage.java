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
            // Wait for map flyout to appear first with shorter timeout
            wait.withTimeout(java.time.Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOf(mapFlyout));
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
        // Try multiple navigation approaches
        boolean navigationSucceeded = false;
        
        // Approach 1: Try the 2-step process (hotel card -> map flyout -> see availability)
        try {
            System.out.println("🔄 Attempting 2-step navigation (hotel card -> map flyout -> details)");
            clickRandomHotelCard();
            clickSeeAvailabilityButton();
            navigationSucceeded = true;
            System.out.println("✅ 2-step navigation succeeded");
        } catch (Exception e) {
            System.out.println("⚠️ 2-step navigation failed: " + e.getMessage());
        }
        
        // Approach 2: Try direct navigation if flyout approach failed
        if (!navigationSucceeded) {
            try {
                System.out.println("🔄 Attempting direct navigation approach");
                navigateToHotelDirectly();
                navigationSucceeded = true;
                System.out.println("✅ Direct navigation succeeded");
            } catch (Exception e) {
                System.out.println("⚠️ Direct navigation failed: " + e.getMessage());
            }
        }
        
        // Approach 3: Try alternative locators if both previous approaches failed
        if (!navigationSucceeded) {
            try {
                System.out.println("🔄 Attempting alternative locator approach");
                navigateUsingAlternativeLocators();
                navigationSucceeded = true;
                System.out.println("✅ Alternative navigation succeeded");
            } catch (Exception e) {
                System.out.println("⚠️ Alternative navigation failed: " + e.getMessage());
            }
        }
        
        if (!navigationSucceeded) {
            throw new RuntimeException("All navigation approaches failed. Unable to navigate to hotel details.");
        }
        
        System.out.println("✅ Hotel navigation completed successfully");
    }
    
    public String getRandomHotelInfo() {
        try {
            WebElement randomCard = selectRandomHotelCard();
            return randomCard.getText();
        } catch (Exception e) {
            return "Could not retrieve hotel information";
        }
    }
    
    /**
     * Fallback method 1: Navigate directly to hotel using different approach
     */
    private void navigateToHotelDirectly() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(hotelResults));
            if (hotelResults.isEmpty()) {
                throw new RuntimeException("No hotel cards available");
            }
            
            Random random = new Random();
            int randomIndex = random.nextInt(hotelResults.size());
            WebElement selectedCard = hotelResults.get(randomIndex);
            
            System.out.println("🎲 Attempting direct click on hotel card at index: " + (randomIndex + 1));
            
            // Try clicking directly on the hotel card instead of the overlay button
            clickElement(selectedCard);
            System.out.println("🏨 Clicked directly on hotel card");
            
            // Wait for navigation
            Thread.sleep(3000);
            
        } catch (Exception e) {
            throw new RuntimeException("Direct navigation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Fallback method 2: Use alternative locators and approaches
     */
    private void navigateUsingAlternativeLocators() {
        try {
            // Try finding clickable links within hotel cards
            wait.until(ExpectedConditions.visibilityOfAllElements(hotelResults));
            
            for (int i = 0; i < hotelResults.size(); i++) {
                WebElement hotelCard = hotelResults.get(i);
                System.out.println("🔍 Searching for clickable elements in hotel card " + (i + 1));
                
                try {
                    // Look for any clickable elements within the card
                    var clickableElements = hotelCard.findElements(org.openqa.selenium.By.cssSelector("a, button, [onclick], [data-track-id]"));
                    
                    if (!clickableElements.isEmpty()) {
                        System.out.println("🎯 Found " + clickableElements.size() + " clickable elements in card " + (i + 1));
                        
                        // Try clicking the first clickable element
                        WebElement firstClickable = clickableElements.get(0);
                        System.out.println("🖱️ Attempting click on: " + firstClickable.getTagName() + " with attributes: " + 
                                         firstClickable.getAttribute("class") + ", " + firstClickable.getAttribute("data-track-id"));
                        
                        clickElement(firstClickable);
                        
                        // Wait to see if navigation occurred
                        Thread.sleep(3000);
                        
                        System.out.println("✅ Alternative navigation attempt completed");
                        return;
                    }
                } catch (Exception cardException) {
                    System.out.println("⚠️ Failed to process card " + (i + 1) + ": " + cardException.getMessage());
                    continue;
                }
            }
            
            throw new RuntimeException("No clickable elements found in any hotel cards");
            
        } catch (Exception e) {
            throw new RuntimeException("Alternative navigation failed: " + e.getMessage(), e);
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
