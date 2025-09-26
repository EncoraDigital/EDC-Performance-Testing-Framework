# Lighthouse Performance Integration Guide

This guide explains how to use the Lighthouse performance audits that have been integrated into your Selenium testing framework.

## 🚀 Quick Start

### Basic Usage (1 line of code)
```java
// Add to any existing test after navigation
LighthouseHelper.quickPerformanceCheck("My Test Page");
```

### Enhanced Usage with Regression Tracking
```java
// Run comprehensive audit with all features
LighthouseRunner.LighthouseMetrics metrics = 
    LighthouseHelper.auditWithRegressionTracking("Homepage Performance");
```

## 📋 Features

### ✅ **Integrated with Existing Selenium Sessions**
- Lighthouse runs on the **same browser instance** as your Selenium tests
- No additional browser launches required
- Preserves all browser state (cookies, login, etc.)

### ✅ **Enhanced Allure Reporting**
- Performance dashboards with visualizations
- Core Web Vitals charts and scorecards
- Trend analysis across test runs
- Regression detection reports

### ✅ **Performance Regression Tracking**
- Automatic baseline establishment
- Historical trend analysis
- Regression severity classification (HIGH/MEDIUM/LOW)
- Performance comparison reports

### ✅ **Multiple Audit Types**
- Quick performance checks
- Comprehensive audits (all categories)
- Performance-only audits (faster execution)
- Desktop vs Mobile comparisons

## 🔧 Implementation Details

### Chrome Configuration
The framework automatically configures Chrome with remote debugging:
```java
// In DriverManager.java - automatically enabled
chromeOptions.addArguments("--remote-debugging-port=9222");
chromeOptions.addArguments("--remote-allow-origins=*");
```

### Dependencies Added
```xml
<!-- Chrome DevTools Protocol -->
<dependency>
    <groupId>com.github.kklisura.cdt</groupId>
    <artifactId>cdt-java-client</artifactId>
    <version>4.0.0</version>
</dependency>

<!-- HTTP Client for Lighthouse API -->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.2.1</version>
</dependency>
```

## 📊 Usage Examples

### 1. Basic Performance Check
```java
@Test
public void testHomepage() {
    navigateToHomepage();
    
    // Add performance check
    LighthouseHelper.quickPerformanceCheck("Homepage Load");
}
```

### 2. Comprehensive Audit with Validation
```java
@Test
public void testWithPerformanceValidation() {
    navigateToPage();
    
    // Run audit
    LighthouseRunner.LighthouseMetrics metrics = LighthouseHelper.auditCurrentPage();
    
    // Validate scores
    LighthouseHelper.validateScores(metrics, 60.0, 80.0, 70.0, 85.0);
    
    // Validate Core Web Vitals
    LighthouseHelper.validateCoreWebVitals(metrics);
}
```

### 3. Regression Tracking
```java
@Test
public void testWithRegressionTracking() {
    navigateToPage();
    
    // Set baseline (run once)
    LighthouseHelper.setPerformanceBaseline("Homepage");
    
    // Regular tests - will compare against baseline
    LighthouseRunner.LighthouseMetrics metrics = 
        LighthouseHelper.auditWithRegressionTracking("Homepage");
}
```

### 4. Desktop vs Mobile Comparison
```java
@Test
public void testMobileVsDesktop() {
    navigateToPage();
    
    // Desktop audit
    LighthouseRunner.LighthouseMetrics desktop = 
        LighthouseHelper.auditCurrentPageDesktop();
    
    // Mobile audit
    LighthouseRunner.LighthouseMetrics mobile = 
        LighthouseHelper.auditCurrentPageMobile();
    
    // Results automatically attached to Allure
}
```

### 5. Performance Throughout User Journey
```java
@Test
public void testUserJourneyPerformance() {
    // Homepage
    navigateToHomepage();
    LighthouseHelper.auditAndAttachToAllure("Homepage");
    
    // After search
    performSearch("New York");
    LighthouseHelper.auditAndAttachToAllure("Search Results");
    
    // After detail view
    clickFirstResult();
    LighthouseHelper.auditAndAttachToAllure("Detail Page");
}
```

## 📈 Allure Integration

### Performance Dashboard
The framework automatically creates rich performance dashboards in Allure:

- **Performance Summary**: Scores, thresholds, and status
- **Core Web Vitals**: FCP, LCP, CLS, TBT with visualizations
- **Performance Scorecard**: Overall grade and recommendations
- **Trend Analysis**: Historical performance trends
- **Regression Reports**: Detailed regression analysis

### Reports Generated
1. **HTML Reports**: Full Lighthouse HTML reports
2. **JSON Reports**: Raw Lighthouse data
3. **Performance Summaries**: Formatted metrics summary
4. **Trend Data**: CSV data for external analysis
5. **Regression Analysis**: Detailed regression reports

## 🔍 Regression Detection

### Automatic Baseline Management
```java
// First run establishes baseline
LighthouseHelper.auditWithRegressionTracking("Homepage");

// Subsequent runs compare against baseline
// Regression detected if:
// - Performance score drops >10%
// - LCP increases >20%
// - FCP increases >20%
// - CLS increases >50%
```

### Regression Severity
- **HIGH**: >30% degradation in key metrics
- **MEDIUM**: Multiple metrics regressed
- **LOW**: Single metric mild regression

### Historical Data Storage
```
performance-history/
├── baseline-metrics.json           # Current baselines
├── Homepage_Test_history.json      # Historical data per test
├── Search_Results_history.json     # Test-specific trends
└── ...
```

## ⚙️ Configuration

### Environment Variables
```bash
# Optional: Override debugging port
export CHROME_DEBUG_PORT=9223

# Optional: Set environment for tracking
export TEST_ENVIRONMENT=staging

# Optional: Set build info for tracking
export BUILD_NUMBER=123
export GIT_COMMIT=abc123
```

### Lighthouse Options
```java
// Custom audit options
Map<String, String> options = new HashMap<>();
options.put("only-categories", "performance,accessibility");
options.put("throttling-method", "provided");
options.put("emulated-form-factor", "desktop");

LighthouseHelper.auditCurrentPage(options);
```

## 🛠️ Troubleshooting

### Common Issues

1. **Lighthouse not found**
   ```bash
   # Install Lighthouse globally
   npm install -g lighthouse
   ```

2. **Chrome debugging port conflicts**
   ```java
   // Change port in DriverManager
   DriverManager.setDebuggingPort(9223);
   ```

3. **Performance audits timing out**
   ```java
   // Use performance-only audit for speed
   LighthouseHelper.auditPerformanceOnly();
   ```

4. **Headless mode issues**
   ```java
   // Remove headless mode for Lighthouse compatibility
   // (already configured in framework)
   ```

### Performance Tips

1. **Use performance-only audits** for faster execution
2. **Run comprehensive audits** only for critical tests
3. **Set appropriate thresholds** for your application
4. **Use regression tracking** to monitor trends over time

## 📁 File Structure

```
src/main/java/com/choice/testing/utils/
├── LighthouseRunner.java              # Core Lighthouse integration
├── LighthouseHelper.java              # Simplified helper methods
├── AllurePerformanceReporter.java     # Enhanced Allure reporting
└── PerformanceRegressionTracker.java  # Regression tracking

src/test/java/com/choice/testing/tests/
├── performance/
│   ├── PerformanceTest.java           # Basic performance tests
│   ├── ComprehensivePerformanceTest.java # Full feature demo
│   └── examples/
│       └── LighthouseIntegrationExample.java # Usage examples
└── desktop/
    ├── ChoiceHotelsTest.java          # Enhanced with Lighthouse
    └── HomePageTest.java              # Enhanced with Lighthouse
```

## 🎯 Best Practices

### 1. **Test Organization**
- Add performance checks to existing functional tests
- Create dedicated performance test suites for comprehensive audits
- Use regression tracking for critical user journeys

### 2. **Threshold Management**
- Set realistic thresholds based on your application
- Use different thresholds for different environments
- Monitor trends rather than absolute scores

### 3. **Reporting Strategy**
- Use quick checks for CI/CD pipelines
- Generate comprehensive reports for release validation
- Share performance dashboards with stakeholders

### 4. **Regression Management**
- Establish baselines after major releases
- Monitor performance trends continuously
- Investigate regressions promptly

## 🔗 Integration Points

### CI/CD Integration
```yaml
# Example GitHub Actions
- name: Run Performance Tests
  run: mvn test -Dtest=ComprehensivePerformanceTest
  
- name: Generate Allure Report
  run: mvn allure:report
  
- name: Check Performance Regressions
  run: |
    if grep -q "Regression Detected.*YES" allure-results/*.json; then
      echo "Performance regression detected!"
      exit 1
    fi
```

### Monitoring Integration
- Export trend data for monitoring systems
- Set up alerts for performance regressions
- Track performance metrics alongside other KPIs

## 📞 Support

For issues or questions:
1. Check the troubleshooting section above
2. Review the example tests in the codebase
3. Check Lighthouse documentation: https://lighthouse-ci.com/
4. Review Chrome DevTools Protocol docs: https://chromedevtools.github.io/devtools-protocol/

## 🚀 Next Steps

1. **Start Simple**: Add `LighthouseHelper.quickPerformanceCheck()` to existing tests
2. **Establish Baselines**: Run tests to establish performance baselines
3. **Monitor Trends**: Review Allure reports for performance insights
4. **Optimize**: Use recommendations to improve performance
5. **Scale**: Add comprehensive audits to critical user journeys

---

**Happy Performance Testing! 🎉**