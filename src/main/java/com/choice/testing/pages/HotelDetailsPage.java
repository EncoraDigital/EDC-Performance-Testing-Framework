package com.choice.testing.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HotelDetailsPage extends BasePage {
    
    @FindBy(css = "h1.property-header-h1")
    private WebElement hotelName;
    
    @FindBy(css = ".hotel-text p")
    private WebElement hotelDescription;
    
    @FindBy(css = ".headband-address.sticky")
    private WebElement hotelAddress;
    
    @FindBy(css = ".headband-phone span:nth-child(2)")
    private WebElement hotelPhone;
    
    @FindBy(css = ".hotel-rating, .property-rating, .rating, .stars")
    private WebElement hotelRating;
    
    @FindBy(css = ".headband .main-price .price")
    private WebElement hotelPrice;
    
    @FindBy(css = ".hotel-amenities, .property-amenities, .amenities")
    private WebElement hotelAmenities;
    
    @FindBy(css = ".hotel-images, .property-images, .gallery, .photos")
    private WebElement hotelImages;
    
    @FindBy(css = "button.choice-button.primary_cta[data-track-id='CheckAvailability']")
    private WebElement viewRoomsButton;
    
    @FindBy(css = ".property-rooms-grid-view-card")
    private List<WebElement> roomCards;
    
    public boolean isOnHotelDetailsPage() {
        try {
            // Check specific patterns for Choice Hotels property pages
            return getCurrentUrl().contains("comfort-inn-hotels") || 
                   getCurrentUrl().contains("/brooklyn/") ||
                   isElementDisplayed(hotelName) ||
                   getPageTitle().toLowerCase().contains("comfort inn");
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getHotelName() {
        try {
            wait.until(ExpectedConditions.visibilityOf(hotelName));
            return hotelName.getText();
        } catch (Exception e) {
            return "Hotel name not found";
        }
    }
    
    public String getHotelDescription() {
        try {
            if (isElementDisplayed(hotelDescription)) {
                return hotelDescription.getText();
            }
            return "Description not available";
        } catch (Exception e) {
            return "Description not found";
        }
    }
    
    public String getHotelAddress() {
        try {
            if (isElementDisplayed(hotelAddress)) {
                return hotelAddress.getText();
            }
            return "Address not available";
        } catch (Exception e) {
            return "Address not found";
        }
    }
    
    public String getHotelPhone() {
        try {
            if (isElementDisplayed(hotelPhone)) {
                return hotelPhone.getText().trim();
            }
            return "Phone not available";
        } catch (Exception e) {
            return "Phone not found";
        }
    }
    
    public String getHotelRating() {
        try {
            if (isElementDisplayed(hotelRating)) {
                return hotelRating.getText();
            }
            return "Rating not available";
        } catch (Exception e) {
            return "Rating not found";
        }
    }
    
    public String getHotelPrice() {
        try {
            if (isElementDisplayed(hotelPrice)) {
                return hotelPrice.getText();
            }
            return "Price not available";
        } catch (Exception e) {
            return "Price not found";
        }
    }
    
    public boolean hasViewRoomsButton() {
        try {
            return isElementDisplayed(viewRoomsButton);
        } catch (Exception e) {
            return false;
        }
    }
    
    public int getRoomCardsCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(roomCards));
            return roomCards.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public void clickViewRooms() {
        try {
            if (hasViewRoomsButton()) {
                clickElement(viewRoomsButton);
                System.out.println("📚 Clicked View Rooms button");
            } else {
                System.out.println("⚠️ View Rooms button not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to click View Rooms button: " + e.getMessage(), e);
        }
    }
    
    public String getHotelDetailsInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Hotel Details:\n");
        info.append("Name: ").append(getHotelName()).append("\n");
        info.append("Address: ").append(getHotelAddress()).append("\n");
        info.append("Rating: ").append(getHotelRating()).append("\n");
        info.append("Price: ").append(getHotelPrice()).append("\n");
        info.append("Phone: ").append(getHotelPhone()).append("\n");
        info.append("Room Options: ").append(getRoomCardsCount()).append(" available\n");
        info.append("Has View Rooms Button: ").append(hasViewRoomsButton()).append("\n");
        return info.toString();
    }
    
    private boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}