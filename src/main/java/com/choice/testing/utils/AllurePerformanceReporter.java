package com.choice.testing.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Allure reporting for Lighthouse performance data
 * Creates rich performance dashboards and trend analysis
 */
public class AllurePerformanceReporter {
    
    private static final Logger logger = LoggerFactory.getLogger(AllurePerformanceReporter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Create a comprehensive performance dashboard in Allure
     */
    public static void createPerformanceDashboard(LighthouseRunner.LighthouseMetrics metrics, 
                                                String testName, String url) {
        try {
            // Create performance summary
            String performanceSummary = createPerformanceSummary(metrics, testName, url);
            attachPerformanceSummary(performanceSummary);
            
            // Create Core Web Vitals visualization
            String coreWebVitals = createCoreWebVitalsChart(metrics);
            attachCoreWebVitalsChart(coreWebVitals);
            
            // Create performance scorecard
            String scorecard = createPerformanceScorecard(metrics);
            attachPerformanceScorecard(scorecard);
            
            // Add Allure environment properties
            addAllureEnvironmentInfo(metrics, url);
            
            // Add performance categories as Allure parameters
            addPerformanceParameters(metrics, testName, url);
            
            logger.info("Performance dashboard created successfully for: {}", testName);
            
        } catch (Exception e) {
            logger.error("Failed to create performance dashboard", e);
        }
    }
    
    /**
     * Create a performance trend analysis across multiple test runs
     */
    public static void createPerformanceTrendAnalysis(List<LighthouseRunner.LighthouseMetrics> historicalMetrics,
                                                    String testName) {
        try {
            if (historicalMetrics.isEmpty()) {
                return;
            }
            
            String trendAnalysis = createTrendAnalysisReport(historicalMetrics, testName);
            attachTrendAnalysis(trendAnalysis);
            
            // Create CSV data for trend visualization
            String csvData = createTrendCsvData(historicalMetrics);
            attachTrendCsvData(csvData);
            
        } catch (Exception e) {
            logger.error("Failed to create trend analysis", e);
        }
    }
    
    /**
     * Compare performance metrics between different environments or versions
     */
    public static void createPerformanceComparison(LighthouseRunner.LighthouseMetrics baselineMetrics,
                                                  LighthouseRunner.LighthouseMetrics currentMetrics,
                                                  String comparisonName) {
        try {
            String comparisonReport = createComparisonReport(baselineMetrics, currentMetrics, comparisonName);
            attachComparisonReport(comparisonReport);
            
            // Add comparison parameters to Allure
            addComparisonParameters(baselineMetrics, currentMetrics, comparisonName);
            
        } catch (Exception e) {
            logger.error("Failed to create performance comparison", e);
        }
    }
    
    private static String createPerformanceSummary(LighthouseRunner.LighthouseMetrics metrics, 
                                                 String testName, String url) {
        StringBuilder summary = new StringBuilder();
        
        summary.append("# Performance Analysis Report\n\n");
        summary.append("## Test Information\n");
        summary.append("- **Test Name**: ").append(testName).append("\n");
        summary.append("- **URL**: ").append(url).append("\n");
        summary.append("- **Timestamp**: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
        
        summary.append("## Lighthouse Scores\n");
        summary.append("| Category | Score | Status |\n");
        summary.append("|----------|--------|--------|\n");
        
        appendScoreRow(summary, "Performance", metrics.getPerformanceScore() * 100, 60);
        appendScoreRow(summary, "Accessibility", metrics.getAccessibilityScore() * 100, 80);
        appendScoreRow(summary, "Best Practices", metrics.getBestPracticesScore() * 100, 70);
        appendScoreRow(summary, "SEO", metrics.getSeoScore() * 100, 80);
        
        summary.append("\n## Core Web Vitals\n");
        summary.append("| Metric | Value | Threshold | Status |\n");
        summary.append("|--------|-------|-----------|--------|\n");
        
        appendWebVitalRow(summary, "First Contentful Paint", metrics.getFirstContentfulPaint(), "ms", 1800);
        appendWebVitalRow(summary, "Largest Contentful Paint", metrics.getLargestContentfulPaint(), "ms", 2500);
        appendWebVitalRow(summary, "Speed Index", metrics.getSpeedIndex(), "ms", 3400);
        appendWebVitalRow(summary, "Total Blocking Time", metrics.getTotalBlockingTime(), "ms", 200);
        appendWebVitalRow(summary, "Cumulative Layout Shift", metrics.getCumulativeLayoutShift(), "", 0.1);
        
        return summary.toString();
    }
    
    private static void appendScoreRow(StringBuilder summary, String category, double score, double threshold) {
        String status = score >= threshold ? "✅ PASS" : "❌ FAIL";
        summary.append("| ").append(category).append(" | ")
               .append(String.format("%.1f%%", score)).append(" | ")
               .append(status).append(" |\n");
    }
    
    private static void appendWebVitalRow(StringBuilder summary, String metric, double value, String unit, double threshold) {
        String status = value <= threshold ? "✅ GOOD" : value <= threshold * 1.5 ? "⚠️ NEEDS IMPROVEMENT" : "❌ POOR";
        summary.append("| ").append(metric).append(" | ")
               .append(String.format("%.0f", value)).append(" ").append(unit).append(" | ")
               .append(threshold).append(" ").append(unit).append(" | ")
               .append(status).append(" |\n");
    }
    
    private static String createCoreWebVitalsChart(LighthouseRunner.LighthouseMetrics metrics) {
        // Create a simple ASCII chart for Core Web Vitals
        StringBuilder chart = new StringBuilder();
        
        chart.append("# Core Web Vitals Visualization\n\n");
        chart.append("```\n");
        chart.append("Core Web Vitals Performance Chart\n");
        chart.append("=====================================\n\n");
        
        // FCP Chart
        chart.append("First Contentful Paint (FCP): ").append(String.format("%.0f ms", metrics.getFirstContentfulPaint())).append("\n");
        chart.append(createProgressBar(metrics.getFirstContentfulPaint(), 3000, 50)).append("\n\n");
        
        // LCP Chart
        chart.append("Largest Contentful Paint (LCP): ").append(String.format("%.0f ms", metrics.getLargestContentfulPaint())).append("\n");
        chart.append(createProgressBar(metrics.getLargestContentfulPaint(), 4000, 50)).append("\n\n");
        
        // CLS Chart
        chart.append("Cumulative Layout Shift (CLS): ").append(String.format("%.3f", metrics.getCumulativeLayoutShift())).append("\n");
        chart.append(createProgressBar(metrics.getCumulativeLayoutShift() * 1000, 250, 50)).append("\n\n");
        
        chart.append("```\n");
        
        return chart.toString();
    }
    
    private static String createProgressBar(double value, double max, int width) {
        int filled = (int) Math.min(width, (value / max) * width);
        StringBuilder bar = new StringBuilder();
        
        bar.append("[");
        for (int i = 0; i < width; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("] ");
        bar.append(String.format("%.1f%%", Math.min(100, (value / max) * 100)));
        
        return bar.toString();
    }
    
    private static String createPerformanceScorecard(LighthouseRunner.LighthouseMetrics metrics) {
        StringBuilder scorecard = new StringBuilder();
        
        scorecard.append("# Performance Scorecard\n\n");
        scorecard.append("## Overall Performance Grade\n");
        
        double avgScore = (metrics.getPerformanceScore() + metrics.getAccessibilityScore() + 
                          metrics.getBestPracticesScore() + metrics.getSeoScore()) / 4 * 100;
        
        String grade = getPerformanceGrade(avgScore);
        scorecard.append("### Grade: ").append(grade).append(" (").append(String.format("%.1f%%", avgScore)).append(")\n\n");
        
        scorecard.append("## Detailed Breakdown\n");
        scorecard.append("- 🚀 **Performance**: ").append(getScoreEmoji(metrics.getPerformanceScore() * 100))
                 .append(" ").append(String.format("%.1f%%", metrics.getPerformanceScore() * 100)).append("\n");
        scorecard.append("- ♿ **Accessibility**: ").append(getScoreEmoji(metrics.getAccessibilityScore() * 100))
                 .append(" ").append(String.format("%.1f%%", metrics.getAccessibilityScore() * 100)).append("\n");
        scorecard.append("- 🛡️ **Best Practices**: ").append(getScoreEmoji(metrics.getBestPracticesScore() * 100))
                 .append(" ").append(String.format("%.1f%%", metrics.getBestPracticesScore() * 100)).append("\n");
        scorecard.append("- 🔍 **SEO**: ").append(getScoreEmoji(metrics.getSeoScore() * 100))
                 .append(" ").append(String.format("%.1f%%", metrics.getSeoScore() * 100)).append("\n\n");
        
        scorecard.append("## Recommendations\n");
        scorecard.append(generateRecommendations(metrics));
        
        return scorecard.toString();
    }
    
    private static String getPerformanceGrade(double score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }
    
    private static String getScoreEmoji(double score) {
        if (score >= 90) return "🟢";
        if (score >= 70) return "🟡";
        return "🔴";
    }
    
    private static String generateRecommendations(LighthouseRunner.LighthouseMetrics metrics) {
        StringBuilder recommendations = new StringBuilder();
        
        if (metrics.getPerformanceScore() * 100 < 60) {
            recommendations.append("- 🚀 **Improve Performance**: Consider optimizing images, minifying CSS/JS, and enabling compression\n");
        }
        
        if (metrics.getAccessibilityScore() * 100 < 80) {
            recommendations.append("- ♿ **Enhance Accessibility**: Add alt text to images, improve color contrast, and ensure keyboard navigation\n");
        }
        
        if (metrics.getBestPracticesScore() * 100 < 70) {
            recommendations.append("- 🛡️ **Follow Best Practices**: Use HTTPS, avoid deprecated APIs, and ensure console is error-free\n");
        }
        
        if (metrics.getSeoScore() * 100 < 80) {
            recommendations.append("- 🔍 **Optimize SEO**: Add meta descriptions, improve heading structure, and ensure mobile-friendliness\n");
        }
        
        if (metrics.getLargestContentfulPaint() > 2500) {
            recommendations.append("- ⚡ **Reduce LCP**: Optimize images and critical rendering path for faster loading\n");
        }
        
        if (metrics.getCumulativeLayoutShift() > 0.1) {
            recommendations.append("- 📐 **Fix Layout Shifts**: Set size attributes on images and avoid inserting content above existing content\n");
        }
        
        if (recommendations.length() == 0) {
            recommendations.append("✅ **Excellent Performance**: All metrics are within recommended thresholds!\n");
        }
        
        return recommendations.toString();
    }
    
    private static String createTrendAnalysisReport(List<LighthouseRunner.LighthouseMetrics> historicalMetrics, String testName) {
        StringBuilder trend = new StringBuilder();
        
        trend.append("# Performance Trend Analysis\n\n");
        trend.append("## Test: ").append(testName).append("\n");
        trend.append("## Data Points: ").append(historicalMetrics.size()).append(" test runs\n\n");
        
        if (historicalMetrics.size() >= 2) {
            LighthouseRunner.LighthouseMetrics latest = historicalMetrics.get(historicalMetrics.size() - 1);
            LighthouseRunner.LighthouseMetrics previous = historicalMetrics.get(historicalMetrics.size() - 2);
            
            trend.append("## Recent Changes\n");
            trend.append("| Metric | Previous | Current | Change |\n");
            trend.append("|--------|----------|---------|--------|\n");
            
            appendTrendRow(trend, "Performance", previous.getPerformanceScore() * 100, latest.getPerformanceScore() * 100);
            appendTrendRow(trend, "Accessibility", previous.getAccessibilityScore() * 100, latest.getAccessibilityScore() * 100);
            appendTrendRow(trend, "Best Practices", previous.getBestPracticesScore() * 100, latest.getBestPracticesScore() * 100);
            appendTrendRow(trend, "SEO", previous.getSeoScore() * 100, latest.getSeoScore() * 100);
            appendTrendRow(trend, "FCP (ms)", previous.getFirstContentfulPaint(), latest.getFirstContentfulPaint());
            appendTrendRow(trend, "LCP (ms)", previous.getLargestContentfulPaint(), latest.getLargestContentfulPaint());
        }
        
        return trend.toString();
    }
    
    private static void appendTrendRow(StringBuilder trend, String metric, double previous, double current) {
        double change = current - previous;
        String changeStr;
        String indicator;
        
        if (Math.abs(change) < 0.1) {
            changeStr = "No change";
            indicator = "➖";
        } else if (change > 0) {
            changeStr = String.format("+%.1f", change);
            // For scores, positive is good; for timing metrics, negative is good
            indicator = metric.contains("ms") ? "📈" : "📈";
        } else {
            changeStr = String.format("%.1f", change);
            indicator = metric.contains("ms") ? "📉" : "📉";
        }
        
        trend.append("| ").append(metric).append(" | ")
             .append(String.format("%.1f", previous)).append(" | ")
             .append(String.format("%.1f", current)).append(" | ")
             .append(indicator).append(" ").append(changeStr).append(" |\n");
    }
    
    private static String createTrendCsvData(List<LighthouseRunner.LighthouseMetrics> historicalMetrics) {
        StringBuilder csv = new StringBuilder();
        
        csv.append("Run,Performance,Accessibility,BestPractices,SEO,FCP,LCP,SpeedIndex,TBT,CLS\n");
        
        for (int i = 0; i < historicalMetrics.size(); i++) {
            LighthouseRunner.LighthouseMetrics metrics = historicalMetrics.get(i);
            csv.append(i + 1).append(",")
               .append(String.format("%.2f", metrics.getPerformanceScore() * 100)).append(",")
               .append(String.format("%.2f", metrics.getAccessibilityScore() * 100)).append(",")
               .append(String.format("%.2f", metrics.getBestPracticesScore() * 100)).append(",")
               .append(String.format("%.2f", metrics.getSeoScore() * 100)).append(",")
               .append(String.format("%.0f", metrics.getFirstContentfulPaint())).append(",")
               .append(String.format("%.0f", metrics.getLargestContentfulPaint())).append(",")
               .append(String.format("%.0f", metrics.getSpeedIndex())).append(",")
               .append(String.format("%.0f", metrics.getTotalBlockingTime())).append(",")
               .append(String.format("%.3f", metrics.getCumulativeLayoutShift())).append("\n");
        }
        
        return csv.toString();
    }
    
    private static String createComparisonReport(LighthouseRunner.LighthouseMetrics baseline,
                                               LighthouseRunner.LighthouseMetrics current,
                                               String comparisonName) {
        StringBuilder comparison = new StringBuilder();
        
        comparison.append("# Performance Comparison Report\n\n");
        comparison.append("## Comparison: ").append(comparisonName).append("\n\n");
        
        comparison.append("| Metric | Baseline | Current | Difference | Status |\n");
        comparison.append("|--------|----------|---------|------------|--------|\n");
        
        appendComparisonRow(comparison, "Performance", baseline.getPerformanceScore() * 100, current.getPerformanceScore() * 100);
        appendComparisonRow(comparison, "Accessibility", baseline.getAccessibilityScore() * 100, current.getAccessibilityScore() * 100);
        appendComparisonRow(comparison, "Best Practices", baseline.getBestPracticesScore() * 100, current.getBestPracticesScore() * 100);
        appendComparisonRow(comparison, "SEO", baseline.getSeoScore() * 100, current.getSeoScore() * 100);
        appendComparisonRow(comparison, "FCP (ms)", baseline.getFirstContentfulPaint(), current.getFirstContentfulPaint());
        appendComparisonRow(comparison, "LCP (ms)", baseline.getLargestContentfulPaint(), current.getLargestContentfulPaint());
        
        return comparison.toString();
    }
    
    private static void appendComparisonRow(StringBuilder comparison, String metric, double baseline, double current) {
        double difference = current - baseline;
        String status;
        
        if (Math.abs(difference) < 1) {
            status = "➖ No Change";
        } else if (metric.contains("ms")) {
            // For timing metrics, lower is better
            status = difference < 0 ? "✅ Improved" : "❌ Regressed";
        } else {
            // For scores, higher is better
            status = difference > 0 ? "✅ Improved" : "❌ Regressed";
        }
        
        comparison.append("| ").append(metric).append(" | ")
                  .append(String.format("%.1f", baseline)).append(" | ")
                  .append(String.format("%.1f", current)).append(" | ")
                  .append(String.format("%+.1f", difference)).append(" | ")
                  .append(status).append(" |\n");
    }
    
    // Allure attachment methods
    
    @Attachment(value = "Performance Summary", type = "text/markdown")
    private static String attachPerformanceSummary(String summary) {
        return summary;
    }
    
    @Attachment(value = "Core Web Vitals Chart", type = "text/markdown")
    private static String attachCoreWebVitalsChart(String chart) {
        return chart;
    }
    
    @Attachment(value = "Performance Scorecard", type = "text/markdown")
    private static String attachPerformanceScorecard(String scorecard) {
        return scorecard;
    }
    
    @Attachment(value = "Performance Trend Analysis", type = "text/markdown")
    private static String attachTrendAnalysis(String trend) {
        return trend;
    }
    
    @Attachment(value = "Trend Data", type = "text/csv")
    private static String attachTrendCsvData(String csvData) {
        return csvData;
    }
    
    @Attachment(value = "Performance Comparison", type = "text/markdown")
    private static String attachComparisonReport(String comparison) {
        return comparison;
    }
    
    private static void addAllureEnvironmentInfo(LighthouseRunner.LighthouseMetrics metrics, String url) {
        try {
            Map<String, String> environment = new HashMap<>();
            environment.put("Test URL", url);
            environment.put("Performance Score", String.format("%.1f%%", metrics.getPerformanceScore() * 100));
            environment.put("Accessibility Score", String.format("%.1f%%", metrics.getAccessibilityScore() * 100));
            environment.put("Lighthouse Version", "Latest");
            environment.put("Test Timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // This would ideally write to allure-results/environment.properties
            // For now, we'll add as parameters
            environment.forEach(Allure::parameter);
            
        } catch (Exception e) {
            logger.warn("Failed to add environment info", e);
        }
    }
    
    private static void addPerformanceParameters(LighthouseRunner.LighthouseMetrics metrics, String testName, String url) {
        Allure.parameter("Test Name", testName);
        Allure.parameter("URL", url);
        Allure.parameter("Performance Score", String.format("%.1f%%", metrics.getPerformanceScore() * 100));
        Allure.parameter("Accessibility Score", String.format("%.1f%%", metrics.getAccessibilityScore() * 100));
        Allure.parameter("Best Practices Score", String.format("%.1f%%", metrics.getBestPracticesScore() * 100));
        Allure.parameter("SEO Score", String.format("%.1f%%", metrics.getSeoScore() * 100));
        Allure.parameter("First Contentful Paint", String.format("%.0f ms", metrics.getFirstContentfulPaint()));
        Allure.parameter("Largest Contentful Paint", String.format("%.0f ms", metrics.getLargestContentfulPaint()));
        Allure.parameter("Cumulative Layout Shift", String.format("%.3f", metrics.getCumulativeLayoutShift()));
    }
    
    private static void addComparisonParameters(LighthouseRunner.LighthouseMetrics baseline,
                                              LighthouseRunner.LighthouseMetrics current,
                                              String comparisonName) {
        Allure.parameter("Comparison Name", comparisonName);
        Allure.parameter("Performance Change", 
            String.format("%+.1f%%", (current.getPerformanceScore() - baseline.getPerformanceScore()) * 100));
        Allure.parameter("Accessibility Change", 
            String.format("%+.1f%%", (current.getAccessibilityScore() - baseline.getAccessibilityScore()) * 100));
    }
}