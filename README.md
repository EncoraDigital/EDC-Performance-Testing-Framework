# E2E Testing Framework

A comprehensive end-to-end testing framework for measuring, monitoring, and improving end-user experience across desktop and mobile platforms.

## 🚀 Features

- **Cross-Platform Testing**: Desktop browsers (Chrome, Firefox) and Mobile devices (iOS, Android)
- **Page Object Model (POM)**: Maintainable and scalable test architecture
- **Performance Monitoring**: Integration with Lighthouse CI and Chrome DevTools Protocol
- **Network Traffic Analysis**: BrowserMob Proxy and OWASP ZAP integration
- **Comprehensive Reporting**: Allure Reports with trend analysis and Grafana dashboards
- **Parallel Execution**: ThreadLocal WebDriver management for concurrent test execution

## 🏗️ Technology Stack

### Core Framework
- **Java 11+** - Programming language
- **Maven** - Dependency management and build tool
- **TestNG** - Test execution framework

### Testing Tools
- **Selenium WebDriver 4** - Desktop browser automation
- **Appium** - Mobile application testing
- **WebDriverManager** - Automatic driver management

### Performance & Monitoring
- **Lighthouse CI** - Performance auditing
- **Chrome DevTools Protocol** - Performance metrics extraction
- **BrowserMob Proxy** - Network traffic capture
- **OWASP ZAP** - Security testing integration

### Reporting & Analytics
- **Allure Reports** - Test execution reporting
- **Grafana** - Historical metrics tracking
- **JSON** - Data serialization and configuration

## 📁 Project Structure

```
e2e-testing-framework/
├── pom.xml                              # Maven configuration
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── choice/
│   │               └── testing/
│   │                   ├── pages/       # Page Object Models
│   │                   ├── utils/       # Utility classes
│   │                   ├── config/      # Configuration management
│   │                   └── drivers/     # WebDriver management
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── choice/
│       │           └── testing/
│       │               ├── tests/       # Test cases
│       │               │   ├── desktop/ # Desktop browser tests
│       │               │   └── mobile/  # Mobile device tests
│       │               └── base/        # Base test classes
│       └── resources/
│           ├── config.properties        # Test configuration
│           ├── testng.xml              # TestNG suite configuration
│           └── testdata/               # Test data files
├── drivers/                            # WebDriver binaries (auto-managed)
├── reports/                            # Generated test reports
└── README.md
```

## 🔧 Prerequisites

- **Java 11 or higher**
- **Maven 3.6+**
- **Chrome/Firefox** browsers installed
- **Android SDK** (for mobile testing)
- **Xcode** (for iOS testing on macOS)
- **Appium Server** (for mobile testing)

## ⚙️ Installation

### 1. Clone the repository
```bash
git clone Repository https://github.com/your-username/e2e-testing-framework.git
cd e2e-testing-framework
```

### 2. Install dependencies
```bash
mvn clean install
```

### 3. Verify installation
```bash
mvn clean compile test-compile
```

### 4. Configure test environment
Edit `src/test/resources/config.properties`:
```properties
# Update URLs and configurations for your application
base.url=https://your-app.com
mobile.base.url=https://m.your-app.com
```

## 🏃‍♂️ Running Tests

### Desktop Browser Tests
```bash
# Run all desktop tests
mvn test

# Run with specific browser
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox

# Run specific test class
mvn test -Dtest=HomePageTest

# Run specific test method
mvn test -Dtest=HomePageTest#testHomePageLoad
```

### Mobile Tests (Future Implementation)
```bash
# Android tests
mvn test -Dplatform=android

# iOS tests  
mvn test -Dplatform=ios
```

### Parallel Execution
```bash
# Run tests in parallel (configured in testng.xml)
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

## 📊 Test Configuration

### TestNG Configuration (`testng.xml`)
```xml
<suite name="E2E Testing Suite" parallel="tests" thread-count="3">
    <test name="Desktop Chrome Tests">
        <parameter name="browser" value="chrome"/>
        <classes>
            <class name="com.choice.testing.tests.desktop.HomePageTest"/>
        </classes>
    </test>
</suite>
```

### Browser Configuration
Modify `config.properties` to change default settings:
```properties
default.browser=chrome
default.timeout=30
base.url=https://example.com
```

## 📈 Reporting

### Allure Reports
```bash
# Generate Allure report
mvn allure:report

# Serve report locally
mvn allure:serve
```

### View Reports
- **Allure Reports**: `target/site/allure-maven-plugin/index.html`
- **Surefire Reports**: `target/surefire-reports/`

## 🎯 Writing Tests

### Creating a New Page Object
```java
@Component
public class LoginPage extends BasePage {
    
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password") 
    private WebElement passwordField;
    
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    public void login(String username, String password) {
        sendKeys(usernameField, username);
        sendKeys(passwordField, password);
        clickElement(loginButton);
    }
}
```

### Creating a New Test
```java
@Epic("User Authentication")
@Feature("Login Functionality")
public class LoginTest extends BaseTest {
    
    @Test
    @Description("Verify successful login with valid credentials")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage();
        HomePage homePage = new HomePage();
        
        loginPage.login("testuser", "password123");
        
        Assert.assertTrue(homePage.isUserLoggedIn(), 
            "User should be logged in successfully");
    }
}
```

## 🔍 Debugging

### Enable Debug Logging
Add to your test method:
```java
System.setProperty("webdriver.chrome.verboseLogging", "true");
```

### Common Issues

1. **WebDriver not found**
   - Solution: WebDriverManager automatically handles this
   - Manual: Download drivers to `drivers/` folder

2. **Tests timing out**
   - Increase timeout in `config.properties`
   - Check element locators

3. **Port conflicts**
   - Ensure Appium server is running on correct port
   - Check `appium.server.url` in config

## 🚀 Future Enhancements

### Phase 2 Implementation
- [ ] Complete Appium mobile testing integration
- [ ] BrowserMob Proxy for network monitoring
- [ ] Chrome DevTools Protocol performance metrics
- [ ] Lighthouse CI integration
- [ ] OWASP ZAP security testing

### Phase 3 Implementation  
- [ ] Grafana dashboard setup
- [ ] Historical trend analysis
- [ ] Performance regression detection
- [ ] CI/CD pipeline integration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/new-feature`
5. Submit a pull request