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
    private static int debuggingPort = 9222;

    public static void initializeWebDriver(String browserType) {
        switch (browserType.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = getChromeOptionsWithRemoteDebugging();
                webDriver.set(new ChromeDriver(chromeOptions));
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                webDriver.set(new FirefoxDriver());
                break;
                
            default:
                throw new IllegalArgumentException("Browser not supported: " + browserType);
        }
        
        getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        // Skip window maximize for headless mode performance
        
        // No startup delay for maximum speed
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
        
        getMobileDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }
    
    private static java.util.Map<String, Object> getChromeOptionsForMobile() {
        java.util.Map<String, Object> chromeOptions = new java.util.HashMap<>();
        java.util.List<String> args = new java.util.ArrayList<>();
        
        args.add("--no-sandbox");
        args.add("--disable-dev-shm-usage");
        args.add("--disable-gpu");
        args.add("--disable-extensions");
        
        chromeOptions.put("args", args);
        
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

    private static ChromeOptions getChromeOptionsWithRemoteDebugging() {
        ChromeOptions chromeOptions = new ChromeOptions();
        
        // Enable remote debugging for Lighthouse integration
        chromeOptions.addArguments("--remote-debugging-port=" + debuggingPort);
        chromeOptions.addArguments("--remote-allow-origins=*");
        
        // Performance optimization options
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-plugins");
        chromeOptions.addArguments("--disable-web-security");
        chromeOptions.addArguments("--disable-features=VizDisplayCompositor");
        chromeOptions.addArguments("--disable-background-timer-throttling");
        chromeOptions.addArguments("--disable-renderer-backgrounding");
        chromeOptions.addArguments("--disable-backgrounding-occluded-windows");
        
        // Optional: Remove headless for Lighthouse (Lighthouse works better with visible browser)
        // chromeOptions.addArguments("--headless");
        
        return chromeOptions;
    }
    
    public static int getDebuggingPort() {
        return debuggingPort;
    }
    
    public static void setDebuggingPort(int port) {
        debuggingPort = port;
    }
}
