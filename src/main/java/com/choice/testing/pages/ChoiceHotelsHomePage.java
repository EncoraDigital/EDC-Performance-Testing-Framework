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
    
    // @FindBy(css = "input[name*='destination'], #destination-input, .destination-field")
    // private WebElement destinationInput;

    @FindBy(css = "input[placeholder='Where to?']")
    private WebElement destinationInput;
    
    @FindBy(css = "button[id='checkInDate'], button[data-track-id='searchFormCheckInDate']")
    private WebElement checkinDateInput;
    
    @FindBy(css = "button[id='checkOutDate'], button[aria-label*='Check-Out Date']")
    private WebElement checkoutDateInput;
    
    @FindBy(css = "button[data-track-id='searchFormRoomsandGuests'], button[class*='occupancy-button-trigger']")
    private WebElement roomsDropdown;
    
    @FindBy(css = "input[id='adults-input'], input[type='number'][max='8']")
    private WebElement adultsDropdown;
    
    @FindBy(css = "button[data-track-id='FindHotelsBTN'], button[type='submit'][class*='primary_cta']")
    private WebElement searchButton;
    
    // Alternative selectors for common patterns
    @FindBy(xpath = "//a[contains(text(), 'Find') and contains(text(), 'Hotel')]")
    private WebElement findHotelLinkXPath;
    
    @FindBy(xpath = "//select[contains(@id, 'hotel') or contains(@name, 'hotel')]//option[contains(text(), 'All')]")
    private WebElement allHotelsOption;
    
    public void navigateToHomePage() {
        driver.get("https://www.choicehotels.com/");
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
            // Click check-in date button to open date picker
            wait.until(ExpectedConditions.elementToBeClickable(checkinDateInput));
            clickElement(checkinDateInput);
            System.out.println("✅ Clicked check-in date button");
            
            // Wait for date picker to open and try to set date
            Thread.sleep(1000);
            
            // Try to find and click the date in the calendar
            try {
                // Look for the specific date in the calendar
                String dayOfMonth = String.valueOf(checkinDate.getDayOfMonth());
                org.openqa.selenium.WebElement dateElement = driver.findElement(
                    org.openqa.selenium.By.xpath("//button[contains(@aria-label, '" + dayOfMonth + "') or text()='" + dayOfMonth + "']")
                );
                clickElement(dateElement);
                System.out.println("✅ Selected check-in date: " + checkinStr);
            } catch (Exception dateEx) {
                System.out.println("⚠️  Could not find specific date in calendar, using default");
                // Press Escape to close picker
                checkinDateInput.sendKeys(org.openqa.selenium.Keys.ESCAPE);
            }
            
            // Small delay before checkout date
            Thread.sleep(500);
            
            // Click check-out date button
            wait.until(ExpectedConditions.elementToBeClickable(checkoutDateInput));
            clickElement(checkoutDateInput);
            System.out.println("✅ Clicked check-out date button");
            
            Thread.sleep(1000);
            
            // Try to find and click checkout date
            try {
                String checkoutDay = String.valueOf(checkoutDate.getDayOfMonth());
                org.openqa.selenium.WebElement checkoutDateElement = driver.findElement(
                    org.openqa.selenium.By.xpath("//button[contains(@aria-label, '" + checkoutDay + "') or text()='" + checkoutDay + "']")
                );
                clickElement(checkoutDateElement);
                System.out.println("✅ Selected check-out date: " + checkoutStr);
            } catch (Exception dateEx) {
                System.out.println("⚠️  Could not find checkout date in calendar, using default");
                checkoutDateInput.sendKeys(org.openqa.selenium.Keys.ESCAPE);
            }
            
        } catch (Exception e) {
            System.out.println("⚠️  Date selection failed: " + e.getMessage());
        }
    }
    
    public void selectRoomsAndGuests(int rooms, int adults) {
        try {
            // Click the rooms & guests dropdown button to open the occupancy dropdown
            wait.until(ExpectedConditions.elementToBeClickable(roomsDropdown));
            clickElement(roomsDropdown);
            System.out.println("✅ Clicked Rooms & Guests dropdown");
            
            // Wait for dropdown to open
            Thread.sleep(1000);
            
            // Set adults count using the number input
            try {
                wait.until(ExpectedConditions.elementToBeClickable(adultsDropdown));
                
                // Clear the current value and enter new value
                adultsDropdown.clear();
                adultsDropdown.sendKeys(String.valueOf(adults));
                System.out.println("✅ Set adults to: " + adults);
                
                // Alternative approach using the +/- buttons if direct input fails
            } catch (Exception inputEx) {
                System.out.println("⚠️  Direct input failed, trying +/- buttons");
                try {
                    // Find current value
                    String currentValue = adultsDropdown.getAttribute("value");
                    int currentAdults = Integer.parseInt(currentValue);
                    
                    if (adults > currentAdults) {
                        // Click + button to increase
                        org.openqa.selenium.WebElement plusButton = driver.findElement(
                            org.openqa.selenium.By.xpath("//button[@data-track-id='adultsSpinButtonIncrement']")
                        );
                        for (int i = currentAdults; i < adults; i++) {
                            clickElement(plusButton);
                            Thread.sleep(200);
                        }
                    } else if (adults < currentAdults) {
                        // Click - button to decrease  
                        org.openqa.selenium.WebElement minusButton = driver.findElement(
                            org.openqa.selenium.By.xpath("//button[@data-track-id='adultsSpinButtonDecrement']")
                        );
                        for (int i = currentAdults; i > adults; i--) {
                            clickElement(minusButton);
                            Thread.sleep(200);
                        }
                    }
                    System.out.println("✅ Set adults to " + adults + " using +/- buttons");
                } catch (Exception buttonEx) {
                    System.out.println("⚠️  Could not adjust adults count: " + buttonEx.getMessage());
                }
            }
            
            // For rooms, we might need to handle it differently - let's look for a rooms input/selector
            try {
                org.openqa.selenium.WebElement roomsInput = driver.findElement(
                    org.openqa.selenium.By.xpath("//input[@id='rooms-input' or contains(@id, 'rooms')]")
                );
                roomsInput.clear();
                roomsInput.sendKeys(String.valueOf(rooms));
                System.out.println("✅ Set rooms to: " + rooms);
            } catch (Exception roomsEx) {
                System.out.println("⚠️  Could not find rooms input, using default (1 room)");
            }
            
            // Click outside or on a "Done" button to close the dropdown
            try {
                // Look for a Done/Apply button
                org.openqa.selenium.WebElement doneButton = driver.findElement(
                    org.openqa.selenium.By.xpath("//button[contains(text(), 'Done') or contains(text(), 'Apply') or @aria-label='Close']")
                );
                clickElement(doneButton);
                System.out.println("✅ Closed occupancy dropdown");
            } catch (Exception closeEx) {
                // If no Done button, click outside the dropdown
                driver.findElement(org.openqa.selenium.By.tagName("body")).click();
                System.out.println("✅ Clicked outside to close dropdown");
            }
            
        } catch (Exception e) {
            System.out.println("⚠️  Could not set rooms/guests: " + e.getMessage());
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

        try {
            clickFindHotelInNav();
            Thread.sleep(2000);

            selectAllTypeHotels();
            Thread.sleep(1500);

            enterDestination(destination);
            Thread.sleep(2000);

            enterDates(checkinDays, checkoutDays);
            selectRoomsAndGuests(rooms, adults);
            clickSearch();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("⚠️ Error during hotel search: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("🏨 Hotel search completed!");
    }    
}

