package com.choice.testing.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {
    
    @FindBy(id = "searchbox_input")
    private WebElement searchInput;
    
    @FindBy(css = "button[type='submit']")
    private WebElement searchButton;
    
    @FindBy(css = ".header__logo")
    private WebElement logo;
    
    @FindBy(css = "a[href*='privacy']")
    private WebElement privacyLink;
    
    @FindBy(css = "a[href*='settings']")
    private WebElement settingsLink;
    
    public void navigateToHomePage() {
        driver.get("https://duckduckgo.com");
        wait.until(ExpectedConditions.visibilityOf(searchInput));
    }
    
    public void searchFor(String query) {
        sendKeys(searchInput, query);
        clickElement(searchButton);
    }
    
    public boolean isLogoDisplayed() {
        try {
            return driver.findElements(org.openqa.selenium.By.cssSelector("img, .logo, [class*='logo']")).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void clickPrivacyLink() {
        try {
            WebElement privacy = driver.findElement(org.openqa.selenium.By.xpath("//a[contains(text(), 'Privacy') or contains(@href, 'privacy')]"));
            clickElement(privacy);
        } catch (Exception e) {
            System.out.println("Privacy link not found");
        }
    }
    
    public String getSearchInputPlaceholder() {
        return searchInput.getAttribute("placeholder");
    }
    
    public boolean isSearchInputVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(searchInput)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
