package com.choice.testing.drivers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Random;

public class DriverManager {
    private static final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriver> mobileDriver = new ThreadLocal<>();
    private static final Random random = new Random();

    public static void initializeWebDriver(String browserType) {
        switch (browserType.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                
                // Performance optimization options
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--disable-plugins");
                chromeOptions.addArguments("--disable-images");
                chromeOptions.addArguments("--remote-allow-origins=*");
                
                // Basic anti-detection options
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.setExperimentalOption("excludeSwitches", java.util.Arrays.asList("enable-automation"));
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                
                webDriver.set(new ChromeDriver(chromeOptions));
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                webDriver.set(new FirefoxDriver());
                break;
                
            default:
                throw new IllegalArgumentException("Browser not supported: " + browserType);
        }
        
        getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        getWebDriver().manage().window().maximize();
        
        // Reduced startup delay for performance
        try {
            Thread.sleep(1000); // 1 second delay only
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void initializeMobileDriver(String platformName, String deviceName) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        // Common capabilities
        capabilities.setCapability("appium:newCommandTimeout", 300);
        capabilities.setCapability("appium:connectHardwareKeyboard", true);
        
        switch (platformName.toLowerCase()) {
            case "android":
                capabilities.setCapability("platformName", "Android");
                capabilities.setCapability("appium:deviceName", deviceName != null ? deviceName : "Pixel_6_API_33");
                capabilities.setCapability("appium:automationName", "UiAutomator2");
                capabilities.setCapability("appium:browserName", "Chrome");
                capabilities.setCapability("appium:chromeOptions", getChromeOptionsForMobile());
                
                mobileDriver.set(new AndroidDriver(new URL("http://localhost:4723"), capabilities));
                break;
                
            case "ios":
                capabilities.setCapability("platformName", "iOS");
                capabilities.setCapability("appium:deviceName", deviceName != null ? deviceName : "iPhone 14");
                capabilities.setCapability("appium:automationName", "XCUITest");
                capabilities.setCapability("appium:browserName", "Safari");
                
                mobileDriver.set(new IOSDriver(new URL("http://localhost:4723"), capabilities));
                break;
                
            default:
                throw new IllegalArgumentException("Mobile platform not supported: " + platformName);
        }
        
        getMobileDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }
    
    private static java.util.Map<String, Object> getChromeOptionsForMobile() {
        java.util.Map<String, Object> chromeOptions = new java.util.HashMap<>();
        java.util.List<String> args = new java.util.ArrayList<>();
        
        args.add("--no-sandbox");
        args.add("--disable-dev-shm-usage");
        args.add("--disable-blink-features=AutomationControlled");
        
        chromeOptions.put("args", args);
        chromeOptions.put("excludeSwitches", java.util.Arrays.asList("enable-automation"));
        chromeOptions.put("useAutomationExtension", false);
        
        return chromeOptions;
    }
    public static WebDriver getWebDriver() {
        return webDriver.get();
    }

    public static AppiumDriver getMobileDriver() {
        return mobileDriver.get();
    }

    public static void quitWebDriver() {
        if (webDriver.get() != null) {
            webDriver.get().quit();
            webDriver.remove();
        }
    }

    public static void quitMobileDriver() {
        if (mobileDriver.get() != null) {
            mobileDriver.get().quit();
            mobileDriver.remove();
        }
    }

    // Human-like delay utility method
    public static void humanDelay() {
        humanDelay(500, 2000);
    }
    
    public static void humanDelay(int minMs, int maxMs) {
        try {
            int delay = minMs + random.nextInt(maxMs - minMs);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
