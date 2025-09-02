package com.choice.testing.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LighthouseRunner {
    
    private static final String REPORTS_DIR = "reports/lighthouse";
    private static final int TIMEOUT_SECONDS = 120;
    
    public static class LighthouseMetrics {
        private double performanceScore;
        private double accessibilityScore;
        private double bestPracticesScore;
        private double seoScore;
        private double firstContentfulPaint;
        private double largestContentfulPaint;
        private double speedIndex;
        private double totalBlockingTime;
        private double cumulativeLayoutShift;
        private String reportPath;
        
        // Getters and setters
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
        
        public String getReportPath() { return reportPath; }
        public void setReportPath(String reportPath) { this.reportPath = reportPath; }
        
        @Override
        public String toString() {
            return String.format(
                "LighthouseMetrics{performance=%.1f, accessibility=%.1f, bestPractices=%.1f, seo=%.1f, FCP=%.0fms, LCP=%.0fms}",
                performanceScore * 100, accessibilityScore * 100, bestPracticesScore * 100, seoScore * 100,
                firstContentfulPaint, largestContentfulPaint
            );
        }
    }
    
    public static LighthouseMetrics runLighthouseAudit(String url) throws Exception {
        return runLighthouseAudit(url, null);
    }
    
    public static LighthouseMetrics runLighthouseAudit(String url, Map<String, String> options) throws Exception {
        return runLighthouseAuditWithRetry(url, options, 3, 10000); // 3 retries with 10s delay
    }
    
    private static LighthouseMetrics runLighthouseAuditWithRetry(String url, Map<String, String> options, int maxRetries, int delayMs) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("Running Lighthouse audit for: " + url + " (Attempt " + attempt + "/" + maxRetries + ")");
                
                // Add delay between attempts (except first)
                if (attempt > 1) {
                    System.out.println("Waiting " + delayMs + "ms before retry...");
                    Thread.sleep(delayMs);
                }
                
                return executeAudit(url, options);
                
            } catch (Exception e) {
                lastException = e;
                System.out.println("Attempt " + attempt + " failed: " + e.getMessage());
                
                if (attempt == maxRetries) {
                    System.out.println("All retry attempts exhausted");
                    break;
                }
                
                // Exponential backoff
                delayMs *= 2;
            }
        }
        
        throw new RuntimeException("Lighthouse audit failed after " + maxRetries + " attempts", lastException);
    }
    
    
    private static LighthouseMetrics executeAudit(String url, Map<String, String> options) throws Exception {
        // Create reports directory if it doesn't exist
        createReportsDirectory();
        
        // Generate unique name for the report
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sanitizedUrl = url.replaceAll("[^a-zA-Z0-9]", "_");
        String reportFileName = String.format("lighthouse_report_%s_%s", sanitizedUrl, timestamp);
        
        // FIX: Don't add extension here, Lighthouse adds it automatically
        String outputBasePath = REPORTS_DIR + "/" + reportFileName;
        String jsonReportPath = outputBasePath + ".report.json";  // Lighthouse adds .report
        String htmlReportPath = outputBasePath + ".report.html";  // Lighthouse adds .report
        
        // Build Lighthouse command
        List<String> command = buildLighthouseCommand(url, outputBasePath, htmlReportPath, options);
        
        // Execute Lighthouse
        Process process = executeCommand(command);
        
        // Verify that JSON report was generated
        File jsonReport = new File(jsonReportPath);
        if (!jsonReport.exists()) {
            // Debug: list generated files
            File reportsDir = new File(REPORTS_DIR);
            System.out.println("Files in reports directory:");
            if (reportsDir.exists()) {
                for (File file : reportsDir.listFiles()) {
                    System.out.println("  " + file.getName());
                }
            }
            throw new RuntimeException("Lighthouse report was not generated: " + jsonReportPath);
        }
        
        // Parse metrics from JSON report
        LighthouseMetrics metrics = parseMetricsFromReport(jsonReportPath);
        metrics.setReportPath(htmlReportPath);
        
        System.out.println("Lighthouse audit completed: " + metrics);
        return metrics;
    }
    
    private static void createReportsDirectory() throws IOException {
        Path reportsPath = Paths.get(REPORTS_DIR);
        if (!Files.exists(reportsPath)) {
            Files.createDirectories(reportsPath);
        }
    }
    
    private static List<String> buildLighthouseCommand(String url, String outputBasePath, String htmlPath, Map<String, String> options) {
        List<String> command = new ArrayList<>();
        
        // Use nvm node path to ensure we use the correct Node.js version
        String homeDir = System.getProperty("user.home");
        String nvmNodePath = homeDir + "/.nvm/versions/node/v22.15.1/bin/lighthouse";
        
        // Check if nvm lighthouse exists, otherwise fall back to system lighthouse
        File nvmLighthouse = new File(nvmNodePath);
        if (nvmLighthouse.exists()) {
            command.add(nvmNodePath);
        } else {
            command.add("lighthouse");
        }
        command.add(url);
        command.add("--output=json,html");
        command.add("--output-path=" + outputBasePath);  // Without extension, Lighthouse adds it
        
        // Minimal Chrome flags
        StringBuilder chromeFlags = new StringBuilder();
        chromeFlags.append("--no-sandbox ");
        chromeFlags.append("--disable-dev-shm-usage ");
        chromeFlags.append("--window-size=1920,1080 ");
        chromeFlags.append("--disable-extensions ");
        
        command.add("--chrome-flags=" + chromeFlags.toString().trim());
        
        // Simple headers to appear more like a regular browser
        String extraHeaders = "{" +
            "\"Referer\":\"https://www.google.com\"" +
            "}";
        
        command.add("--extra-headers=" + extraHeaders);
        
        // Additional Lighthouse flags for better compatibility
        command.add("--quiet");
        command.add("--no-enable-error-reporting");
        command.add("--max-wait-for-load=45000");  // Wait longer for page load
        command.add("--skip-audits=uses-http2,bf-cache");  // Skip problematic audits
        command.add("--throttling-method=provided");  // Use provided network throttling
        
        // Add additional options if provided
        if (options != null) {
            for (Map.Entry<String, String> entry : options.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    command.add("--" + entry.getKey() + "=" + entry.getValue());
                } else {
                    command.add("--" + entry.getKey());
                }
            }
        }
        
        System.out.println("Executing command: " + String.join(" ", command));
        return command;
    }
    
    private static Process executeCommand(List<String> command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        
        // Set up environment for nvm Node.js version
        Map<String, String> env = processBuilder.environment();
        String homeDir = System.getProperty("user.home");
        
        // Add nvm node path to PATH
        String nvmBinPath = homeDir + "/.nvm/versions/node/v22.15.1/bin";
        String currentPath = env.get("PATH");
        if (currentPath != null) {
            env.put("PATH", nvmBinPath + ":" + currentPath);
        } else {
            env.put("PATH", nvmBinPath);
        }
        
        // Set NVM environment variables
        env.put("NVM_DIR", homeDir + "/.nvm");
        env.put("NODE_VERSION", "v22.15.1");
        
        Process process = processBuilder.start();
        
        // Read output for debugging
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        // Wait for process to complete
        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Lighthouse process timed out after " + TIMEOUT_SECONDS + " seconds");
        }
        
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            System.err.println("Lighthouse output: " + output.toString());
            throw new RuntimeException("Lighthouse failed with exit code: " + exitCode);
        }
        
        return process;
    }
    
    private static LighthouseMetrics parseMetricsFromReport(String jsonReportPath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(jsonReportPath));
        
        LighthouseMetrics metrics = new LighthouseMetrics();
        
        // Extract category scores
        JsonNode categories = rootNode.path("categories");
        metrics.setPerformanceScore(categories.path("performance").path("score").asDouble(0));
        metrics.setAccessibilityScore(categories.path("accessibility").path("score").asDouble(0));
        metrics.setBestPracticesScore(categories.path("best-practices").path("score").asDouble(0));
        metrics.setSeoScore(categories.path("seo").path("score").asDouble(0));
        
        // Extract performance metrics
        JsonNode audits = rootNode.path("audits");
        metrics.setFirstContentfulPaint(audits.path("first-contentful-paint").path("numericValue").asDouble(0));
        metrics.setLargestContentfulPaint(audits.path("largest-contentful-paint").path("numericValue").asDouble(0));
        metrics.setSpeedIndex(audits.path("speed-index").path("numericValue").asDouble(0));
        metrics.setTotalBlockingTime(audits.path("total-blocking-time").path("numericValue").asDouble(0));
        metrics.setCumulativeLayoutShift(audits.path("cumulative-layout-shift").path("numericValue").asDouble(0));
        
        return metrics;
    }
    
    public static LighthouseMetrics runMobileAudit(String url) throws Exception {
        Map<String, String> mobileOptions = new HashMap<>();
        mobileOptions.put("emulated-form-factor", "mobile");
        mobileOptions.put("throttling-method", "simulate");
        return runLighthouseAudit(url, mobileOptions);
    }
    
    public static LighthouseMetrics runDesktopAudit(String url) throws Exception {
        Map<String, String> desktopOptions = new HashMap<>();
        desktopOptions.put("emulated-form-factor", "desktop");
        desktopOptions.put("throttling-method", "simulate");
        return runLighthouseAudit(url, desktopOptions);
    }

    // Add method in tests to open in browser automatically
    public static void openReportInBrowser(String reportPath) {
      try {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        
        if (os.contains("mac")) {
            pb = new ProcessBuilder("open", reportPath);
        } else if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", "start", reportPath);
        } else {
            pb = new ProcessBuilder("xdg-open", reportPath);
        }
        
        pb.start();
        System.out.println("Report opened in browser: " + reportPath);
        
    } catch (Exception e) {
        System.out.println("Could not auto-open report: " + e.getMessage());
        System.out.println("Please manually open: " + reportPath);
    }
  }

    /**
     * Attach Lighthouse HTML report to Allure report
     */
    public static void attachLighthouseReportToAllure(LighthouseMetrics metrics, String testName) throws IOException {
        if (metrics.getReportPath() != null && new File(metrics.getReportPath()).exists()) {
            File htmlReportFile = new File(metrics.getReportPath());
            byte[] htmlContent = Files.readAllBytes(htmlReportFile.toPath());
            
            Allure.addAttachment(
                testName + " - Lighthouse HTML Report",
                "text/html",
                new java.io.ByteArrayInputStream(htmlContent),
                ".html"
            );
            
            // Also attach the JSON report for raw data
            String jsonPath = metrics.getReportPath().replace(".report.html", ".report.json");
            File jsonReportFile = new File(jsonPath);
            if (jsonReportFile.exists()) {
                byte[] jsonContent = Files.readAllBytes(jsonReportFile.toPath());
                Allure.addAttachment(
                    testName + " - Lighthouse JSON Report",
                    "application/json",
                    new java.io.ByteArrayInputStream(jsonContent),
                    ".json"
                );
            }
        }
    }

    /**
     * Attach Lighthouse metrics summary as text attachment
     */
    @Attachment(value = "Lighthouse Metrics Summary", type = "text/plain")
    public static String attachLighthouseMetrics(LighthouseMetrics metrics, String url) {
        StringBuilder summary = new StringBuilder();
        summary.append("Lighthouse Performance Report\n");
        summary.append("=============================\n");
        summary.append("URL: ").append(url).append("\n");
        summary.append("Timestamp: ").append(new java.util.Date()).append("\n\n");
        
        summary.append("Category Scores:\n");
        summary.append("- Performance: ").append(String.format("%.1f%%", metrics.getPerformanceScore() * 100)).append("\n");
        summary.append("- Accessibility: ").append(String.format("%.1f%%", metrics.getAccessibilityScore() * 100)).append("\n");
        summary.append("- Best Practices: ").append(String.format("%.1f%%", metrics.getBestPracticesScore() * 100)).append("\n");
        summary.append("- SEO: ").append(String.format("%.1f%%", metrics.getSeoScore() * 100)).append("\n\n");
        
        summary.append("Core Web Vitals:\n");
        summary.append("- First Contentful Paint (FCP): ").append(String.format("%.0f ms", metrics.getFirstContentfulPaint())).append("\n");
        summary.append("- Largest Contentful Paint (LCP): ").append(String.format("%.0f ms", metrics.getLargestContentfulPaint())).append("\n");
        summary.append("- Speed Index: ").append(String.format("%.0f ms", metrics.getSpeedIndex())).append("\n");
        summary.append("- Total Blocking Time (TBT): ").append(String.format("%.0f ms", metrics.getTotalBlockingTime())).append("\n");
        summary.append("- Cumulative Layout Shift (CLS): ").append(String.format("%.3f", metrics.getCumulativeLayoutShift())).append("\n");
        
        return summary.toString();
    }

    /**
     * Complete Allure integration - attaches both reports and metrics
     */
    public static void attachAllReportsToAllure(LighthouseMetrics metrics, String url, String testName) throws IOException {
        // Attach metrics summary
        attachLighthouseMetrics(metrics, url);
        
        // Attach HTML and JSON reports
        attachLighthouseReportToAllure(metrics, testName);
        
        // Add step information to Allure
        Allure.step("Lighthouse Performance Audit Completed", () -> {
            Allure.parameter("URL", url);
            Allure.parameter("Performance Score", String.format("%.1f%%", metrics.getPerformanceScore() * 100));
            Allure.parameter("Accessibility Score", String.format("%.1f%%", metrics.getAccessibilityScore() * 100));
            Allure.parameter("Best Practices Score", String.format("%.1f%%", metrics.getBestPracticesScore() * 100));
            Allure.parameter("SEO Score", String.format("%.1f%%", metrics.getSeoScore() * 100));
        });
    }
}
