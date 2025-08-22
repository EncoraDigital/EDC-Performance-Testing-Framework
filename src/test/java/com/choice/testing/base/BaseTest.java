package com.choice.testing.base;

import com.choice.testing.config.ConfigManager;
import com.choice.testing.drivers.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class BaseTest {
    
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional String browser) {
        String browserType = browser != null ? browser : ConfigManager.getProperty("default.browser");
        DriverManager.initializeWebDriver(browserType);
    }
    
    @AfterMethod
    public void tearDown() {
        DriverManager.quitWebDriver();
    }
}
