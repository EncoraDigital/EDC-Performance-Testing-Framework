package com.choice.testing.tests.performance;

import com.choice.testing.utils.LighthouseRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LighthouseBasicTest {
    
    @Test
    public void testLighthouseBasicIntegration() throws Exception {
        String testUrl = "https://www.google.com";
        
        System.out.println("Testing Lighthouse integration with: " + testUrl);
        
        LighthouseRunner.LighthouseMetrics metrics = LighthouseRunner.runLighthouseAudit(testUrl);
        
        Assert.assertNotNull(metrics, "Metrics should not be null");
        Assert.assertTrue(metrics.getPerformanceScore() >= 0, "Performance score should be >= 0");
        
        System.out.println("Lighthouse audit completed successfully!");
        System.out.println("Results: " + metrics.toString());
        System.out.println("HTML Report: " + metrics.getReportPath());
    }
}
