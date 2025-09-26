package com.choice.testing.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Tracks performance metrics over time and detects regressions
 * Stores historical data and provides regression analysis
 */
public class PerformanceRegressionTracker {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceRegressionTracker.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PERFORMANCE_DATA_DIR = "performance-history";
    private static final String BASELINE_FILE = "baseline-metrics.json";
    
    public static class PerformanceDataPoint {
        private String testName;
        private String url;
        private String timestamp;
        private String gitCommit;
        private String buildNumber;
        private String environment;
        private double performanceScore;
        private double accessibilityScore;
        private double bestPracticesScore;
        private double seoScore;
        private double firstContentfulPaint;
        private double largestContentfulPaint;
        private double speedIndex;
        private double totalBlockingTime;
        private double cumulativeLayoutShift;
        
        // Default constructor for Jackson
        public PerformanceDataPoint() {}
        
        public PerformanceDataPoint(LighthouseRunner.LighthouseMetrics metrics, String testName, String url) {
            this.testName = testName;
            this.url = url;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.gitCommit = System.getProperty("git.commit", "unknown");
            this.buildNumber = System.getProperty("build.number", "local");
            this.environment = System.getProperty("test.environment", "test");
            
            this.performanceScore = metrics.getPerformanceScore();
            this.accessibilityScore = metrics.getAccessibilityScore();
            this.bestPracticesScore = metrics.getBestPracticesScore();
            this.seoScore = metrics.getSeoScore();
            this.firstContentfulPaint = metrics.getFirstContentfulPaint();
            this.largestContentfulPaint = metrics.getLargestContentfulPaint();
            this.speedIndex = metrics.getSpeedIndex();
            this.totalBlockingTime = metrics.getTotalBlockingTime();
            this.cumulativeLayoutShift = metrics.getCumulativeLayoutShift();
        }
        
        // Getters and setters
        public String getTestName() { return testName; }
        public void setTestName(String testName) { this.testName = testName; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getGitCommit() { return gitCommit; }
        public void setGitCommit(String gitCommit) { this.gitCommit = gitCommit; }
        
        public String getBuildNumber() { return buildNumber; }
        public void setBuildNumber(String buildNumber) { this.buildNumber = buildNumber; }
        
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        
        public double getPerformanceScore() { return performanceScore; }
        public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }
        
        public double getAccessibilityScore() { return accessibilityScore; }
        public void setAccessibilityScore(double accessibilityScore) { this.accessibilityScore = accessibilityScore; }
        
        public double getBestPracticesScore() { return bestPracticesScore; }
        public void setBestPracticesScore(double bestPracticesScore) { this.bestPracticesScore = bestPracticesScore; }
        
        public double getSeoScore() { return seoScore; }
        public void setSeoScore(double seoScore) { this.seoScore = seoScore; }
        
        public double getFirstContentfulPaint() { return firstContentfulPaint; }
        public void setFirstContentfulPaint(double firstContentfulPaint) { this.firstContentfulPaint = firstContentfulPaint; }
        
        public double getLargestContentfulPaint() { return largestContentfulPaint; }
        public void setLargestContentfulPaint(double largestContentfulPaint) { this.largestContentfulPaint = largestContentfulPaint; }
        
        public double getSpeedIndex() { return speedIndex; }
        public void setSpeedIndex(double speedIndex) { this.speedIndex = speedIndex; }
        
        public double getTotalBlockingTime() { return totalBlockingTime; }
        public void setTotalBlockingTime(double totalBlockingTime) { this.totalBlockingTime = totalBlockingTime; }
        
        public double getCumulativeLayoutShift() { return cumulativeLayoutShift; }
        public void setCumulativeLayoutShift(double cumulativeLayoutShift) { this.cumulativeLayoutShift = cumulativeLayoutShift; }
    }
    
    public static class RegressionAnalysis {
        private boolean hasRegression;
        private List<String> regressionDetails;
        private Map<String, Double> performanceChanges;
        private String severity;
        
        public RegressionAnalysis() {
            this.regressionDetails = new ArrayList<>();
            this.performanceChanges = new HashMap<>();
        }
        
        public boolean hasRegression() { return hasRegression; }
        public void setHasRegression(boolean hasRegression) { this.hasRegression = hasRegression; }
        
        public List<String> getRegressionDetails() { return regressionDetails; }
        public void setRegressionDetails(List<String> regressionDetails) { this.regressionDetails = regressionDetails; }
        
        public Map<String, Double> getPerformanceChanges() { return performanceChanges; }
        public void setPerformanceChanges(Map<String, Double> performanceChanges) { this.performanceChanges = performanceChanges; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }
    
    /**
     * Record performance metrics for regression tracking
     */
    public static void recordPerformanceMetrics(LighthouseRunner.LighthouseMetrics metrics, 
                                              String testName, String url) {
        try {
            ensureDirectoryExists();
            
            PerformanceDataPoint dataPoint = new PerformanceDataPoint(metrics, testName, url);
            
            // Store in historical data
            storeHistoricalData(dataPoint);
            
            // Update baseline if this is a successful run
            updateBaselineIfNeeded(dataPoint);
            
            logger.info("Performance metrics recorded for: {}", testName);
            
        } catch (Exception e) {
            logger.error("Failed to record performance metrics", e);
        }
    }
    
    /**
     * Analyze current metrics against historical data for regressions
     */
    public static RegressionAnalysis analyzeRegression(LighthouseRunner.LighthouseMetrics currentMetrics,
                                                     String testName, String url) {
        RegressionAnalysis analysis = new RegressionAnalysis();
        
        try {
            // Get baseline metrics
            PerformanceDataPoint baseline = getBaselineMetrics(testName);
            if (baseline == null) {
                logger.info("No baseline found for {}, establishing current run as baseline", testName);
                recordPerformanceMetrics(currentMetrics, testName, url);
                return analysis;
            }
            
            // Get recent historical data for trend analysis
            List<PerformanceDataPoint> recentHistory = getRecentHistory(testName, 10);
            
            // Analyze against baseline
            analyzeAgainstBaseline(analysis, currentMetrics, baseline);
            
            // Analyze trends
            analyzeTrends(analysis, currentMetrics, recentHistory);
            
            // Determine severity
            determineSeverity(analysis);
            
            // Record current metrics
            recordPerformanceMetrics(currentMetrics, testName, url);
            
            // Attach regression analysis to Allure
            attachRegressionAnalysisToAllure(analysis, testName);
            
        } catch (Exception e) {
            logger.error("Failed to analyze regression", e);
        }
        
        return analysis;
    }
    
    /**
     * Create comprehensive performance report with trend analysis
     */
    public static void createPerformanceReport(String testName) {
        try {
            List<PerformanceDataPoint> history = getHistoricalData(testName);
            if (history.isEmpty()) {
                logger.info("No historical data available for performance report: {}", testName);
                return;
            }
            
            // Convert to LighthouseMetrics for AllurePerformanceReporter
            List<LighthouseRunner.LighthouseMetrics> metricsHistory = new ArrayList<>();
            for (PerformanceDataPoint point : history) {
                LighthouseRunner.LighthouseMetrics metrics = convertToLighthouseMetrics(point);
                metricsHistory.add(metrics);
            }
            
            // Create trend analysis
            AllurePerformanceReporter.createPerformanceTrendAnalysis(metricsHistory, testName);
            
            logger.info("Performance report created for: {}", testName);
            
        } catch (Exception e) {
            logger.error("Failed to create performance report", e);
        }
    }
    
    /**
     * Set new baseline metrics (e.g., after successful deployment)
     */
    public static void setNewBaseline(LighthouseRunner.LighthouseMetrics metrics, 
                                    String testName, String url) {
        try {
            ensureDirectoryExists();
            
            PerformanceDataPoint baseline = new PerformanceDataPoint(metrics, testName, url);
            baseline.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, PerformanceDataPoint> baselines = loadBaselines();
            baselines.put(testName, baseline);
            saveBaselines(baselines);
            
            logger.info("New baseline set for: {}", testName);
            
        } catch (Exception e) {
            logger.error("Failed to set new baseline", e);
        }
    }
    
    private static void ensureDirectoryExists() throws IOException {
        Path dir = Paths.get(PERFORMANCE_DATA_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }
    
    private static void storeHistoricalData(PerformanceDataPoint dataPoint) throws IOException {
        String fileName = dataPoint.getTestName().replaceAll("[^a-zA-Z0-9]", "_") + "_history.json";
        Path filePath = Paths.get(PERFORMANCE_DATA_DIR, fileName);
        
        List<PerformanceDataPoint> history = new ArrayList<>();
        if (Files.exists(filePath)) {
            try {
                String content = Files.readString(filePath);
                history = objectMapper.readValue(content, new TypeReference<List<PerformanceDataPoint>>(){});
            } catch (Exception e) {
                logger.warn("Failed to load existing history, starting fresh", e);
            }
        }
        
        history.add(dataPoint);
        
        // Keep only last 100 entries to prevent file from growing too large
        if (history.size() > 100) {
            history = history.subList(history.size() - 100, history.size());
        }
        
        String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(history);
        Files.writeString(filePath, jsonContent);
    }
    
    private static void updateBaselineIfNeeded(PerformanceDataPoint dataPoint) throws IOException {
        Map<String, PerformanceDataPoint> baselines = loadBaselines();
        
        // Update baseline if this is the first run or if performance is significantly better
        if (!baselines.containsKey(dataPoint.getTestName())) {
            baselines.put(dataPoint.getTestName(), dataPoint);
            saveBaselines(baselines);
        }
    }
    
    private static Map<String, PerformanceDataPoint> loadBaselines() throws IOException {
        Path baselineFile = Paths.get(PERFORMANCE_DATA_DIR, BASELINE_FILE);
        
        if (!Files.exists(baselineFile)) {
            return new HashMap<>();
        }
        
        try {
            String content = Files.readString(baselineFile);
            return objectMapper.readValue(content, new TypeReference<Map<String, PerformanceDataPoint>>(){});
        } catch (Exception e) {
            logger.warn("Failed to load baselines, starting fresh", e);
            return new HashMap<>();
        }
    }
    
    private static void saveBaselines(Map<String, PerformanceDataPoint> baselines) throws IOException {
        Path baselineFile = Paths.get(PERFORMANCE_DATA_DIR, BASELINE_FILE);
        String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(baselines);
        Files.writeString(baselineFile, jsonContent);
    }
    
    private static PerformanceDataPoint getBaselineMetrics(String testName) throws IOException {
        Map<String, PerformanceDataPoint> baselines = loadBaselines();
        return baselines.get(testName);
    }
    
    private static List<PerformanceDataPoint> getHistoricalData(String testName) throws IOException {
        String fileName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_history.json";
        Path filePath = Paths.get(PERFORMANCE_DATA_DIR, fileName);
        
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        
        try {
            String content = Files.readString(filePath);
            return objectMapper.readValue(content, new TypeReference<List<PerformanceDataPoint>>(){});
        } catch (Exception e) {
            logger.warn("Failed to load historical data", e);
            return new ArrayList<>();
        }
    }
    
    private static List<PerformanceDataPoint> getRecentHistory(String testName, int count) throws IOException {
        List<PerformanceDataPoint> history = getHistoricalData(testName);
        if (history.size() <= count) {
            return history;
        }
        return history.subList(history.size() - count, history.size());
    }
    
    private static void analyzeAgainstBaseline(RegressionAnalysis analysis, 
                                             LighthouseRunner.LighthouseMetrics current,
                                             PerformanceDataPoint baseline) {
        
        // Define regression thresholds (percentage decrease)
        double scoreRegressionThreshold = 10.0; // 10% decrease in scores
        double timingRegressionThreshold = 20.0; // 20% increase in timing metrics
        
        // Check performance score
        double perfChange = ((current.getPerformanceScore() - baseline.getPerformanceScore()) / baseline.getPerformanceScore()) * 100;
        analysis.getPerformanceChanges().put("Performance Score", perfChange);
        
        if (perfChange < -scoreRegressionThreshold) {
            analysis.setHasRegression(true);
            analysis.getRegressionDetails().add(String.format("Performance score regressed by %.1f%% (from %.1f%% to %.1f%%)", 
                Math.abs(perfChange), baseline.getPerformanceScore() * 100, current.getPerformanceScore() * 100));
        }
        
        // Check LCP
        double lcpChange = ((current.getLargestContentfulPaint() - baseline.getLargestContentfulPaint()) / baseline.getLargestContentfulPaint()) * 100;
        analysis.getPerformanceChanges().put("Largest Contentful Paint", lcpChange);
        
        if (lcpChange > timingRegressionThreshold) {
            analysis.setHasRegression(true);
            analysis.getRegressionDetails().add(String.format("LCP regressed by %.1f%% (from %.0fms to %.0fms)", 
                lcpChange, baseline.getLargestContentfulPaint(), current.getLargestContentfulPaint()));
        }
        
        // Check FCP
        double fcpChange = ((current.getFirstContentfulPaint() - baseline.getFirstContentfulPaint()) / baseline.getFirstContentfulPaint()) * 100;
        analysis.getPerformanceChanges().put("First Contentful Paint", fcpChange);
        
        if (fcpChange > timingRegressionThreshold) {
            analysis.setHasRegression(true);
            analysis.getRegressionDetails().add(String.format("FCP regressed by %.1f%% (from %.0fms to %.0fms)", 
                fcpChange, baseline.getFirstContentfulPaint(), current.getFirstContentfulPaint()));
        }
        
        // Check CLS
        if (baseline.getCumulativeLayoutShift() > 0) {
            double clsChange = ((current.getCumulativeLayoutShift() - baseline.getCumulativeLayoutShift()) / baseline.getCumulativeLayoutShift()) * 100;
            analysis.getPerformanceChanges().put("Cumulative Layout Shift", clsChange);
            
            if (clsChange > 50.0) { // 50% increase in CLS is significant
                analysis.setHasRegression(true);
                analysis.getRegressionDetails().add(String.format("CLS regressed by %.1f%% (from %.3f to %.3f)", 
                    clsChange, baseline.getCumulativeLayoutShift(), current.getCumulativeLayoutShift()));
            }
        }
    }
    
    private static void analyzeTrends(RegressionAnalysis analysis, 
                                    LighthouseRunner.LighthouseMetrics current,
                                    List<PerformanceDataPoint> recentHistory) {
        
        if (recentHistory.size() < 3) {
            return; // Need at least 3 data points for trend analysis
        }
        
        // Analyze performance score trend over last few runs
        List<Double> perfScores = new ArrayList<>();
        for (PerformanceDataPoint point : recentHistory.subList(Math.max(0, recentHistory.size() - 5), recentHistory.size())) {
            perfScores.add(point.getPerformanceScore());
        }
        
        // Check if there's a consistent downward trend
        if (isConsistentDownwardTrend(perfScores)) {
            analysis.getRegressionDetails().add("Consistent downward trend detected in performance scores over recent runs");
        }
    }
    
    private static boolean isConsistentDownwardTrend(List<Double> values) {
        if (values.size() < 3) return false;
        
        int downwardMoves = 0;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) < values.get(i - 1)) {
                downwardMoves++;
            }
        }
        
        // If 70% or more moves are downward, consider it a trend
        return (double) downwardMoves / (values.size() - 1) >= 0.7;
    }
    
    private static void determineSeverity(RegressionAnalysis analysis) {
        if (!analysis.hasRegression()) {
            analysis.setSeverity("NONE");
            return;
        }
        
        // Count severe regressions
        int severeRegressions = 0;
        for (Map.Entry<String, Double> entry : analysis.getPerformanceChanges().entrySet()) {
            double change = Math.abs(entry.getValue());
            if (change > 30.0) { // 30% change is severe
                severeRegressions++;
            }
        }
        
        if (severeRegressions > 0) {
            analysis.setSeverity("HIGH");
        } else if (analysis.getRegressionDetails().size() > 2) {
            analysis.setSeverity("MEDIUM");
        } else {
            analysis.setSeverity("LOW");
        }
    }
    
    private static LighthouseRunner.LighthouseMetrics convertToLighthouseMetrics(PerformanceDataPoint point) {
        // Create a LighthouseMetrics object from PerformanceDataPoint
        LighthouseRunner.LighthouseMetrics metrics = new LighthouseRunner.LighthouseMetrics();
        
        metrics.setPerformanceScore(point.getPerformanceScore());
        metrics.setAccessibilityScore(point.getAccessibilityScore());
        metrics.setBestPracticesScore(point.getBestPracticesScore());
        metrics.setSeoScore(point.getSeoScore());
        metrics.setFirstContentfulPaint(point.getFirstContentfulPaint());
        metrics.setLargestContentfulPaint(point.getLargestContentfulPaint());
        metrics.setSpeedIndex(point.getSpeedIndex());
        metrics.setTotalBlockingTime(point.getTotalBlockingTime());
        metrics.setCumulativeLayoutShift(point.getCumulativeLayoutShift());
        
        return metrics;
    }
    
    private static void attachRegressionAnalysisToAllure(RegressionAnalysis analysis, String testName) {
        if (analysis.hasRegression()) {
            StringBuilder report = new StringBuilder();
            report.append("# Performance Regression Analysis\n\n");
            report.append("## Test: ").append(testName).append("\n");
            report.append("## Severity: ").append(analysis.getSeverity()).append("\n\n");
            
            report.append("## Regression Details:\n");
            for (String detail : analysis.getRegressionDetails()) {
                report.append("- ").append(detail).append("\n");
            }
            
            report.append("\n## Performance Changes:\n");
            for (Map.Entry<String, Double> entry : analysis.getPerformanceChanges().entrySet()) {
                report.append("- ").append(entry.getKey()).append(": ")
                      .append(String.format("%+.1f%%", entry.getValue())).append("\n");
            }
            
            Allure.addAttachment("Regression Analysis", "text/markdown", 
                new ByteArrayInputStream(report.toString().getBytes()), ".md");
                
            // Add as Allure parameters
            Allure.parameter("Regression Detected", "YES");
            Allure.parameter("Regression Severity", analysis.getSeverity());
        } else {
            Allure.parameter("Regression Detected", "NO");
        }
    }
}