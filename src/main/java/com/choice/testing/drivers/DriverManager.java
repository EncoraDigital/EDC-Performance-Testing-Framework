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
                
                // Basic options
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
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
