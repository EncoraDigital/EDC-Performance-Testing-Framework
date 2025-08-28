package com.choice.testing.drivers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
                
                // Basic options
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--remote-allow-origins=*");
                
                // Advanced anti-detection options
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.140 Safari/537.36");
                chromeOptions.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                chromeOptions.addArguments("--disable-web-security");
                chromeOptions.addArguments("--allow-running-insecure-content");
                chromeOptions.addArguments("--disable-features=TranslateUI");
                chromeOptions.addArguments("--disable-ipc-flooding-protection");
                chromeOptions.addArguments("--disable-renderer-backgrounding");
                chromeOptions.addArguments("--disable-backgrounding-occluded-windows");
                chromeOptions.addArguments("--disable-client-side-phishing-detection");
                chromeOptions.addArguments("--disable-sync");
                chromeOptions.addArguments("--metrics-recording-only");
                chromeOptions.addArguments("--no-report-upload");
                chromeOptions.addArguments("--disable-default-apps");
                chromeOptions.addArguments("--disable-extensions-file-access-check");
                chromeOptions.addArguments("--disable-extensions-http-throttling");
                chromeOptions.addArguments("--aggressive-cache-discard");
                
                // Additional stealth options to avoid detection
                chromeOptions.addArguments("--disable-logging");
                chromeOptions.addArguments("--disable-background-timer-throttling");
                chromeOptions.addArguments("--disable-backgrounding-occluded-windows");
                chromeOptions.addArguments("--disable-features=TranslateUI,BlinkGenPropertyTrees");
                chromeOptions.addArguments("--disable-hang-monitor");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--disable-prompt-on-repost");
                chromeOptions.addArguments("--disable-domain-reliability");
                chromeOptions.addArguments("--disable-component-update");
                
                // Realistic user agent
                chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.7258.129 Safari/537.36");
                
                // Remove automation indicators
                chromeOptions.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                
                // Configure additional preferences
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("profile.default_content_setting_values.notifications", 2);
                prefs.put("profile.default_content_settings.popups", 0);
                prefs.put("profile.managed_default_content_settings.images", 1); // Allow images to appear more human
                prefs.put("profile.default_content_setting_values.geolocation", 2);
                prefs.put("profile.default_content_setting_values.media_stream", 2);
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                chromeOptions.setExperimentalOption("prefs", prefs);
                
                webDriver.set(new ChromeDriver(chromeOptions));
                
                // Post-initialization scripts to hide WebDriver
                JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
                
                // Hide webdriver property
                js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
                
                // Modify chrome object
                js.executeScript("window.chrome = { runtime: {} };");
                
                // Modify plugins to appear more realistic
                js.executeScript("Object.defineProperty(navigator, 'plugins', {get: () => ({length: 2})});");
                
                // Modify languages
                js.executeScript("Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']})");
                
                // Add more realistic navigator properties
                js.executeScript("Object.defineProperty(navigator, 'permissions', {get: () => ({query: () => Promise.resolve({state: 'granted'})})})");
                js.executeScript("Object.defineProperty(navigator, 'connection', {get: () => ({effectiveType: '4g', type: 'wifi'})})");
                js.executeScript("Object.defineProperty(navigator, 'deviceMemory', {get: () => 8})");
                js.executeScript("Object.defineProperty(navigator, 'hardwareConcurrency', {get: () => 4})");
                
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                webDriver.set(new FirefoxDriver());
                break;
                
            default:
                throw new IllegalArgumentException("Browser not supported: " + browserType);
        }
        
        getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        getWebDriver().manage().window().maximize();
        
        // Random delay to appear more human-like
        try {
            Thread.sleep(2000 + random.nextInt(3000)); // 2-5 second random delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Rest of methods remain the same
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
