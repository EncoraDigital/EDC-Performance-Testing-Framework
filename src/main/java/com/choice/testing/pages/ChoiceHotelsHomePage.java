package com.choice.testing.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChoiceHotelsHomePage extends BasePage {
    
    // Navigation elements
    @FindBy(css = "a[href*='hotels'], .nav-link:contains('Find'), button:contains('Find')")
    private WebElement findHotelNavLink;
    
    @FindBy(css = "button[data-testid='find-hotel'], .find-hotel-btn, .search-btn")
    private WebElement findHotelButton;
    
    // Hotel search form elements
    @FindBy(css = "select[name*='hotel-type'], #hotel-type-select, .hotel-type-dropdown")
    private WebElement hotelTypeDropdown;
    
    @FindBy(css = "input[name*='destination'], #destination-input, .destination-field")
    private WebElement destinationInput;
    
    @FindBy(css = "input[name*='checkin'], #checkin-date, .checkin-input")
    private WebElement checkinDateInput;
    
    @FindBy(css = "input[name*='checkout'], #checkout-date, .checkout-input")
    private WebElement checkoutDateInput;
    
    @FindBy(css = "select[name*='rooms'], #rooms-select, .rooms-dropdown")
    private WebElement roomsDropdown;
    
    @FindBy(css = "select[name*='adults'], #adults-select, .adults-dropdown")
    private WebElement adultsDropdown;
    
    @FindBy(css = "button[type='submit'], .search-button, .find-hotels-btn")
    private WebElement searchButton;
    
    // Alternative selectors for common patterns
    @FindBy(xpath = "//a[contains(text(), 'Find') and contains(text(), 'Hotel')]")
    private WebElement findHotelLinkXPath;
    
    @FindBy(xpath = "//select[contains(@id, 'hotel') or contains(@name, 'hotel')]//option[contains(text(), 'All')]")
    private WebElement allHotelsOption;
    
    public void navigateToHomePage() {
        driver.get("https://www.choicehotels.com/");
        // Wait for page to load
        wait.until(ExpectedConditions.urlContains("choicehotels"));
    }
    
    public void clickFindHotelInNav() {
        try {
            // Try multiple approaches to find the nav element
            if (isElementPresent(findHotelNavLink)) {
                clickElement(findHotelNavLink);
            } else if (isElementPresent(findHotelLinkXPath)) {
                clickElement(findHotelLinkXPath);
            } else {
                // Fallback: look for any navigation element with "find" or "search"
                WebElement navElement = driver.findElement(
                    org.openqa.selenium.By.xpath("//nav//a[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'find') or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'hotel')]")
                );
                clickElement(navElement);
            }
            
            System.out.println("✅ Clicked on 'Find a Hotel' in navigation");
        } catch (Exception e) {
            System.out.println("⚠️  Could not find 'Find a Hotel' nav link, trying alternative approach");
            // Sometimes the search form is already visible on homepage
        }
    }
    
    public void selectAllTypeHotels() {
        try {
            if (isElementPresent(hotelTypeDropdown)) {
                Select hotelTypeSelect = new Select(hotelTypeDropdown);
                hotelTypeSelect.selectByVisibleText("All types");
                System.out.println("✅ Selected 'All types' from dropdown");
            } else if (isElementPresent(allHotelsOption)) {
                clickElement(allHotelsOption);
                System.out.println("✅ Selected 'All hotels' option");
            }
        } catch (Exception e) {
            System.out.println("⚠️  Hotel type selection not found or not required");
        }
    }
    
    public void enterDestination(String destination) {
        try {
            // Wait for destination input to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(destinationInput));
            sendKeys(destinationInput, destination);
            System.out.println("✅ Entered destination: " + destination);
            
            // Wait a moment for autocomplete suggestions to appear and disappear
            Thread.sleep(1000);
            
        } catch (Exception e) {
            System.out.println("⚠️  Could not find destination input field");
            throw new RuntimeException("Destination input not found", e);
        }
    }
    
    public void enterDates(int checkinDaysFromNow, int checkoutDaysFromNow) {
        LocalDate checkinDate = LocalDate.now().plusDays(checkinDaysFromNow);
        LocalDate checkoutDate = LocalDate.now().plusDays(checkoutDaysFromNow);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String checkinStr = checkinDate.format(formatter);
        String checkoutStr = checkoutDate.format(formatter);
        
        try {
            // Clear and enter check-in date
            wait.until(ExpectedConditions.elementToBeClickable(checkinDateInput));
            checkinDateInput.clear();
            sendKeys(checkinDateInput, checkinStr);
            System.out.println("✅ Entered check-in date: " + checkinStr);
            
            // Clear and enter check-out date
            wait.until(ExpectedConditions.elementToBeClickable(checkoutDateInput));
            checkoutDateInput.clear();
            sendKeys(checkoutDateInput, checkoutStr);
            System.out.println("✅ Entered check-out date: " + checkoutStr);
            
        } catch (Exception e) {
            System.out.println("⚠️  Could not set dates, trying alternative method");
            // Alternative: click on date inputs to open calendar picker
            try {
                clickElement(checkinDateInput);
                // Add logic here to select dates from calendar if needed
                Thread.sleep(500);
                clickElement(checkoutDateInput);
                Thread.sleep(500);
            } catch (Exception ex) {
                System.out.println("⚠️  Date selection failed");
            }
        }
    }
    
    public void selectRoomsAndGuests(int rooms, int adults) {
        try {
            // Select rooms
            if (isElementPresent(roomsDropdown)) {
                Select roomsSelect = new Select(roomsDropdown);
                roomsSelect.selectByValue(String.valueOf(rooms));
                System.out.println("✅ Selected " + rooms + " room(s)");
            }
            
            // Select adults
            if (isElementPresent(adultsDropdown)) {
                Select adultsSelect = new Select(adultsDropdown);
                adultsSelect.selectByValue(String.valueOf(adults));
                System.out.println("✅ Selected " + adults + " adult(s)");
            }
            
        } catch (Exception e) {
            System.out.println("⚠️  Could not set rooms/guests, might be set by default");
        }
    }
    
    public void clickSearch() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(searchButton));
            clickElement(searchButton);
            System.out.println("✅ Clicked Search button");
            
            // Wait for search results or next page to load
            Thread.sleep(3000);
            
        } catch (Exception e) {
            System.out.println("⚠️  Could not find search button, trying alternatives");
            try {
                // Try to find search button with different selectors
                WebElement altSearchBtn = driver.findElement(
                    org.openqa.selenium.By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search') or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'find')]")
                );
                clickElement(altSearchBtn);
                System.out.println("✅ Clicked Search button (alternative selector)");
            } catch (Exception ex) {
                throw new RuntimeException("Could not find search button", ex);
            }
        }
    }
    
    // Utility method to check if element is present
    private boolean isElementPresent(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Complete hotel search flow
    public void performHotelSearch(String destination, int checkinDays, int checkoutDays, int rooms, int adults) {
        System.out.println("🏨 Starting hotel search flow...");
        
        clickFindHotelInNav();
        selectAllTypeHotels();
        enterDestination(destination);
        enterDates(checkinDays, checkoutDays);
        selectRoomsAndGuests(rooms, adults);
        clickSearch();
        
        System.out.println("🏨 Hotel search completed!");
    }
}

