package com.choice.testing.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HotelSearchResultsPage extends BasePage {
    
    @FindBy(css = ".hotel-result, .hotel-card, .property-card")
    private List<WebElement> hotelResults;
    
    @FindBy(css = ".results-count, .search-results-header")
    private WebElement resultsHeader;
    
    @FindBy(css = ".no-results, .no-hotels-found")
    private WebElement noResultsMessage;
    
    @FindBy(css = "h1, .page-title")
    private WebElement pageTitle;
    
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
    
    private boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
