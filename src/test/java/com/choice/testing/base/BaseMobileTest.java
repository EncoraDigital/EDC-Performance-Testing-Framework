package com.choice.testing.base;

import com.choice.testing.config.ConfigManager;
import com.choice.testing.drivers.DriverManager;
import io.qameta.allure.Step;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;

public class BaseMobileTest {
    
    @BeforeMethod
    @Parameters({"platform", "deviceName"})
    public void setUp(@Optional("android") String platform, @Optional String deviceName) throws MalformedURLException {
        initializeMobileDriver(platform, deviceName);
    }
    
    @AfterMethod
    public void tearDown() {
        DriverManager.quitMobileDriver();
    }
    
    @Step("Initialize mobile driver for platform: {platform}, device: {deviceName}")
    protected void initializeMobileDriver(String platform, String deviceName) throws MalformedURLException {
        // Use configuration properties if device name not provided
        if (deviceName == null) {
            if ("android".equalsIgnoreCase(platform)) {
                deviceName = ConfigManager.getProperty("android.device.name");
            } else if ("ios".equalsIgnoreCase(platform)) {
                deviceName = ConfigManager.getProperty("ios.device.name");
            }
        }
        
        System.out.println("📱 Initializing mobile driver for platform: " + platform + ", device: " + deviceName);
        DriverManager.initializeMobileDriver(platform, deviceName);
        
        // Navigate to base URL
        String baseUrl = ConfigManager.getProperty("mobile.base.url", "https://www.choicehotels.com/");
        DriverManager.getMobileDriver().get(baseUrl);
        
        System.out.println("📱 Mobile driver initialized and navigated to: " + baseUrl);
    }
    
    @Step("Add mobile delay - removed for performance")
    protected void addMobileDelay() {
        // Mobile delays removed for maximum speed
    }
}