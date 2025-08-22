package com.choice.testing.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class SearchResultsPage extends BasePage {
    
    @FindBy(css = "[data-testid='result']")
    private List<WebElement> searchResults;
    
    @FindBy(css = "input[type='text']")
    private WebElement searchInput;
    
    @FindBy(css = "[data-testid='result-title-a']")
    private List<WebElement> resultTitles;
    
    @FindBy(css = "a[aria-label='DuckDuckGo']")
    private WebElement logoLink;
    
    public boolean hasSearchResults() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(searchResults));
            return searchResults.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    public int getResultsCount() {
        wait.until(ExpectedConditions.visibilityOfAllElements(searchResults));
        return searchResults.size();
    }
    
    public void clickFirstResult() {
        if (!resultTitles.isEmpty()) {
            clickElement(resultTitles.get(0));
        }
    }
    
    public String getFirstResultTitle() {
        if (!resultTitles.isEmpty()) {
            wait.until(ExpectedConditions.visibilityOf(resultTitles.get(0)));
            return resultTitles.get(0).getText();
        }
        return "";
    }
    
    public void goBackToHomePage() {
        clickElement(logoLink);
    }
    
    public void searchAgain(String query) {
        sendKeys(searchInput, query);
        searchInput.submit();
    }
}
