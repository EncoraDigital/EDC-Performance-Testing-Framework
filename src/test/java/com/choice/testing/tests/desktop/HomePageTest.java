package com.choice.testing.tests.desktop;

import com.choice.testing.base.BaseTest;
import com.choice.testing.pages.HomePage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Desktop Web Testing")
@Feature("Home Page")
public class HomePageTest extends BaseTest {
    
    @Test
    @Description("Verify home page loads successfully")
    public void testHomePageLoad() {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();
        
        Assert.assertTrue(homePage.isLogoDisplayed(), "Logo should be displayed");
        Assert.assertTrue(homePage.getPageTitle().contains("Example"), "Page title should contain 'Example'");
    }
    
    @Test
    @Description("Verify search functionality works")
    public void testSearchFunctionality() {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();
        homePage.searchFor("test query");
        
        // Add assertions based on your application
        Assert.assertTrue(homePage.getCurrentUrl().contains("search"), "URL should contain 'search' after searching");
    }
}
