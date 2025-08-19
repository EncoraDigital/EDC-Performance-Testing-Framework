package com.choice.testing.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {
    
    @FindBy(id = "search-input")
    private WebElement searchInput;
    
    @FindBy(css = "button[type='submit']")
    private WebElement searchButton;
    
    @FindBy(className = "logo")
    private WebElement logo;
    
    public void searchFor(String query) {
        sendKeys(searchInput, query);
        clickElement(searchButton);
    }
    
    public boolean isLogoDisplayed() {
        return logo.isDisplayed();
    }
    
    public void navigateToHomePage() {
        driver.get("https://example.com");
    }
}
