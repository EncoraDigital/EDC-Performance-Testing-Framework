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
- **Lighthouse** - Performance auditing with Allure integration ✅
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
- **Node.js 22.15.1+** (via nvm) - Required for Lighthouse
- **Lighthouse CLI** - `npm install -g lighthouse`
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

### 2. Setup Node.js and Lighthouse
```bash
# Install nvm (if not already installed)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# Install and use Node.js 22.15.1
nvm install 22.15.1
nvm use 22.15.1

# Install Lighthouse globally
npm install -g lighthouse
```

### 3. Install Maven dependencies
```bash
mvn clean install
```

### 4. Verify installation
```bash
mvn clean compile test-compile

# Verify Lighthouse installation
lighthouse --version
```

### 5. Configure test environment
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

### Performance Testing with Lighthouse
```bash
# Run basic Lighthouse performance tests
mvn test -Dtest=LighthouseBasicTest

# Run comprehensive performance test suite (includes all features)
mvn test -Dtest=ComprehensivePerformanceTest

# Run specific comprehensive test methods
mvn test -Dtest=ComprehensivePerformanceTest#testFullPerformanceAuditPipeline
mvn test -Dtest=ComprehensivePerformanceTest#testPerformanceRegressionMonitoring
mvn test -Dtest=ComprehensivePerformanceTest#testCrossEnvironmentPerformanceComparison
mvn test -Dtest=ComprehensivePerformanceTest#testUserActionPerformanceImpact
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
```bash
# Open Allure report in browser (recommended)
mvn allure:serve

# Or manually open generated HTML file
open allure-results/html-report/index.html
```

- **Allure Reports**: `allure-results/html-report/index.html`
- **Lighthouse Reports**: `reports/lighthouse/`
- **Surefire Reports**: `target/surefire-reports/`

### Lighthouse Integration Features
The Allure reports now include:
- 📊 **Performance Metrics**: Core Web Vitals (FCP, LCP), Performance scores
- 📄 **HTML Reports**: Interactive Lighthouse audit reports with visual charts
- 💾 **Raw Data**: JSON files with detailed performance data
- 📈 **Parameters**: Key metrics displayed in test overview
- 📋 **Test Steps**: Detailed audit execution steps
- 🔍 **Regression Tracking**: Performance trend analysis across multiple test runs
- 📱 **Device Comparison**: Desktop vs Mobile performance comparison dashboards
- ⚡ **Performance Baselines**: Establish and monitor performance baselines
- 🎯 **User Journey Tracking**: Performance impact analysis of user actions

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

### Creating a Lighthouse Performance Test

#### Basic Lighthouse Test
```java
@Epic("Performance Testing")
@Feature("Lighthouse Integration")
public class PerformanceTest extends BaseTest {

    @Test
    @Story("Website Performance Audit")
    @Description("Performs Lighthouse audit and integrates results with Allure")
    @Severity(SeverityLevel.NORMAL)
    public void testWebsitePerformance() throws Exception {
        String testUrl = "https://your-website.com";
        String testName = "Website Performance Audit";

        Allure.step("Starting Lighthouse audit for: " + testUrl, () -> {
            System.out.println("Running performance audit...");
        });

        LighthouseRunner.LighthouseMetrics metrics =
            LighthouseRunner.runLighthouseAudit(testUrl);

        // Attach reports to Allure
        LighthouseRunner.attachAllReportsToAllure(metrics, testUrl, testName);

        // Assert performance thresholds
        Allure.step("Validating performance metrics", () -> {
            Assert.assertTrue(metrics.getPerformanceScore() >= 0.5,
                "Performance score should be at least 50%");
            Assert.assertTrue(metrics.getAccessibilityScore() >= 0.9,
                "Accessibility score should be at least 90%");
        });
    }
}
```

#### Comprehensive Performance Test with Regression Tracking
```java
@Epic("Performance Testing")
@Feature("Comprehensive Lighthouse Integration")
public class ComprehensivePerformanceTest extends BaseTest {

    @Test
    @Description("Full performance audit with baseline, regression tracking, and user journey")
    public void testFullPerformanceAuditPipeline() throws Exception {
        // Navigate to application
        navigateToChoiceHotels();

        // Set performance baseline
        LighthouseRunner.LighthouseMetrics metrics =
            LighthouseHelper.auditWithRegressionTracking("Homepage Baseline");

        // Validate Core Web Vitals
        LighthouseHelper.validateCoreWebVitals(metrics);

        // Perform user journey with performance tracking
        performHotelSearch("New York, NY");
        LighthouseRunner.LighthouseMetrics journeyMetrics =
            LighthouseHelper.auditPerformanceOnly();

        // Attach all reports to Allure
        String currentUrl = DriverManager.getWebDriver().getCurrentUrl();
        LighthouseRunner.attachAllReportsToAllure(journeyMetrics, currentUrl,
            "User Journey Performance");

        // Generate regression report
        PerformanceRegressionTracker.createPerformanceReport("Performance Trend Analysis");
    }

    @Test
    @Description("Compare desktop vs mobile performance")
    public void testDesktopMobileComparison() throws Exception {
        navigateToChoiceHotels();

        // Desktop audit
        LighthouseRunner.LighthouseMetrics desktopMetrics =
            LighthouseHelper.auditCurrentPageDesktop();

        // Mobile audit
        LighthouseRunner.LighthouseMetrics mobileMetrics =
            LighthouseHelper.auditCurrentPageMobile();

        // Create comparison dashboard
        AllurePerformanceReporter.createPerformanceComparison(
            desktopMetrics, mobileMetrics, "Desktop vs Mobile Performance");
    }
}
```

#### Available Lighthouse Helper Methods
```java
// Performance-only audits (faster execution)
LighthouseHelper.auditPerformanceOnly();

// Full audits with all categories
LighthouseHelper.auditWithRegressionTracking("Test Name");

// Device-specific audits
LighthouseHelper.auditCurrentPageDesktop();
LighthouseHelper.auditCurrentPageMobile();

// Baseline management
LighthouseHelper.setPerformanceBaseline("Baseline Name");

// Validation
LighthouseHelper.validateScores(metrics, perfThreshold, accessThreshold,
    bestPracticesThreshold, seoThreshold);
LighthouseHelper.validateCoreWebVitals(metrics);
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

4. **Lighthouse/Node.js issues**
   - Ensure Node.js 22.15.1+ is active: `nvm use 22.15.1`
   - Verify Lighthouse installation: `lighthouse --version`
   - Check ICU4C compatibility on macOS

## 📋 Lighthouse Setup Instructions

### 💻 Local Development Setup

#### Option 1: Using NVM (Recommended)

##### Step 1: Install NVM
```bash
# macOS/Linux
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# Windows (use Git Bash or WSL)
curl -o- https://raw.githubusercontent.com/coreybutler/nvm-windows/master/install.cmd | cmd
```

##### Step 2: Restart Terminal and Install Node.js
```bash
# Restart your terminal, then:
nvm install v22.15.1
nvm use v22.15.1
nvm alias default v22.15.1
```

##### Step 3: Install Lighthouse Globally
```bash
npm install -g lighthouse
```

##### Step 4: Verify Installation
```bash
node --version          # Should show v22.15.1
npm --version          # Should show npm version
lighthouse --version   # Should show 12.8.2 or later
which lighthouse       # Should show path to lighthouse
```

#### Option 2: Direct Node.js Installation

##### Step 1: Install Node.js
- Download from [nodejs.org](https://nodejs.org/) (LTS version recommended)
- Or use package managers:
  ```bash
  # macOS (Homebrew)
  brew install node
  
  # Ubuntu/Debian
  sudo apt update
  sudo apt install nodejs npm
  
  # CentOS/RHEL
  sudo yum install nodejs npm
  ```

##### Step 2: Install Lighthouse
```bash
npm install -g lighthouse
```

### 🏭 CI/CD Environment Setup

#### GitHub Actions
Create `.github/workflows/lighthouse-tests.yml`:

```yaml
name: Lighthouse Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  lighthouse-tests:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 11
      uses: actions/setup-java@v4
      with:
        java-version: '11'
        distribution: 'temurin'
    
    - name: Set up Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '22.15.1'
    
    - name: Install Lighthouse
      run: |
        npm install -g lighthouse
        lighthouse --version
    
    - name: Install Chrome
      uses: browser-actions/setup-chrome@latest
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run Lighthouse Tests
      run: |
        mvn test -Dtest=LighthouseBasicTest
    
    - name: Upload Lighthouse Reports
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: lighthouse-reports
        path: reports/lighthouse/
```

#### Jenkins Pipeline
Create `Jenkinsfile`:

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8'
        jdk 'JDK 11'
    }
    
    stages {
        stage('Setup Node.js & Lighthouse') {
            steps {
                script {
                    // Install Node.js using NodeJS plugin
                    nodejs(nodeJSInstallationName: 'Node 22.15.1') {
                        sh 'npm install -g lighthouse'
                        sh 'lighthouse --version'
                    }
                }
            }
        }
        
        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Run Lighthouse Tests') {
            steps {
                nodejs(nodeJSInstallationName: 'Node 22.15.1') {
                    sh 'mvn test -Dtest=LighthouseBasicTest'
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'reports/lighthouse',
                        reportFiles: '*.html',
                        reportName: 'Lighthouse Reports'
                    ])
                }
            }
        }
    }
}
```

### 🐳 Docker Setup

#### Dockerfile
Create a `Dockerfile` in your project root:

```dockerfile
FROM node:22.15.1

# Install Java
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk maven && \
    apt-get clean

# Install Chrome
RUN apt-get update && \
    apt-get install -y wget gnupg && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    apt-get clean

# Install Lighthouse globally
RUN npm install -g lighthouse

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Install Maven dependencies
RUN mvn dependency:resolve

# Default command
CMD ["mvn", "test", "-Dtest=LighthouseBasicTest"]
```

#### Docker Compose
Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  lighthouse-tests:
    build: .
    volumes:
      - ./reports:/app/reports
      - ./target:/app/target
    environment:
      - MAVEN_OPTS=-Xmx1024m
    command: mvn test -Dtest=LighthouseBasicTest
```

#### Run with Docker
```bash
# Build and run
docker-compose up --build

# Or run directly
docker build -t lighthouse-tests .
docker run -v $(pwd)/reports:/app/reports lighthouse-tests
```

### 🔧 Troubleshooting

#### Common Issues

##### 1. "lighthouse: command not found"
**Solution:**
```bash
# Check if Node.js is installed
node --version

# Install Lighthouse globally
npm install -g lighthouse

# Check installation
which lighthouse
lighthouse --version
```

##### 2. "Chrome not found" Error
**Solution:**
```bash
# macOS
brew install --cask google-chrome

# Ubuntu/Debian
wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
sudo sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list'
sudo apt update
sudo apt install google-chrome-stable

# CentOS/RHEL
sudo yum install google-chrome-stable
```

##### 3. "Permission denied" on lighthouse
**Solution:**
```bash
# Fix npm permissions (Linux/macOS)
sudo chown -R $(whoami) $(npm config get prefix)/{lib/node_modules,bin,share}

# Or reinstall with correct permissions
npm uninstall -g lighthouse
npm install -g lighthouse
```

##### 4. Java ProcessBuilder issues
**Solution:**
The framework handles NVM paths automatically, but if issues persist:

```bash
# Create symlink (temporary fix)
sudo ln -sf ~/.nvm/versions/node/v22.15.1/bin/lighthouse /usr/local/bin/lighthouse
```

### ✅ Environment-Specific Notes

#### Windows
- Use Git Bash or PowerShell
- May need to run as Administrator for global npm installs
- Chrome path might need manual configuration

#### macOS
- Homebrew is recommended for package management
- No special configuration needed

#### Linux
- May need `sudo` for system package installations
- Ensure Chrome/Chromium is installed
- Check firewall settings for CI environments

#### Cloud Environments (AWS/GCP/Azure)
- Use container-based approaches (Docker)
- Ensure adequate memory allocation (minimum 2GB)
- Configure security groups for headless Chrome

## 🚀 Future Enhancements

### Phase 2 Implementation
- [ ] Complete Appium mobile testing integration
- [ ] BrowserMob Proxy for network monitoring
- [ ] Chrome DevTools Protocol performance metrics
- [x] **Lighthouse integration with Allure reporting** ✅
- [x] **Performance regression tracking** ✅
- [x] **Desktop vs Mobile performance comparison** ✅
- [x] **Core Web Vitals validation** ✅
- [ ] OWASP ZAP security testing

### Phase 3 Implementation
- [ ] Grafana dashboard setup
- [x] **Historical trend analysis** ✅
- [x] **Performance regression detection** ✅
- [x] **Performance baseline management** ✅
- [ ] CI/CD pipeline integration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/new-feature`
5. Submit a pull request