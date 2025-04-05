package com.example.utils;

import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Reporter class for generating performance reports.
 * This class provides methods to generate HTML and JSON reports for performance metrics.
 */
public class PerformanceReporter {

    private static final Logger logger = LogManager.getLogger(PerformanceReporter.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    /**
     * Generates a performance report in JSON format.
     * @param testName The name of the test
     * @return The path to the generated report
     */
    public static String generateJsonReport(String testName) {
        try {
            // Create directory if it doesn't exist
            Path reportDir = Paths.get(Constants.REPORT_PATH, "performance");
            Files.createDirectories(reportDir);
            
            // Create report file
            String timestamp = DATE_FORMAT.format(new Date());
            Path reportFile = reportDir.resolve(testName + "_" + timestamp + ".json");
            
            // Convert metrics to JSON
            JSONObject jsonReport = new JSONObject();
            jsonReport.put("testName", testName);
            jsonReport.put("timestamp", timestamp);
            jsonReport.put("metrics", PerformanceUtils.getAllPerformanceMetrics());
            
            // Write to file
            try (FileWriter writer = new FileWriter(reportFile.toFile())) {
                writer.write(jsonReport.toString(2));
            }
            
            logger.info("Performance JSON report generated: {}", reportFile);
            return reportFile.toString();
        } catch (IOException e) {
            logger.error("Failed to generate performance JSON report: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generates a performance report in HTML format.
     * @param testName The name of the test
     * @return The path to the generated report
     */
    public static String generateHtmlReport(String testName) {
        try {
            // Create directory if it doesn't exist
            Path reportDir = Paths.get(Constants.REPORT_PATH, "performance");
            Files.createDirectories(reportDir);
            
            // Create report file
            String timestamp = DATE_FORMAT.format(new Date());
            Path reportFile = reportDir.resolve(testName + "_" + timestamp + ".html");
            
            // Generate HTML content
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("    <title>Performance Report - ").append(testName).append("</title>\n")
                .append("    <style>\n")
                .append("        body { font-family: Arial, sans-serif; margin: 20px; }\n")
                .append("        h1 { color: #333; }\n")
                .append("        .page-section { margin-bottom: 30px; border: 1px solid #ddd; padding: 15px; border-radius: 5px; }\n")
                .append("        .page-title { background-color: #f5f5f5; padding: 10px; margin-top: 0; }\n")
                .append("        table { border-collapse: collapse; width: 100%; }\n")
                .append("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n")
                .append("        th { background-color: #f2f2f2; }\n")
                .append("        .metric-good { color: green; }\n")
                .append("        .metric-warning { color: orange; }\n")
                .append("        .metric-bad { color: red; }\n")
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <h1>Performance Report - ").append(testName).append("</h1>\n")
                .append("    <p>Generated on: ").append(timestamp).append("</p>\n");
            
            // Add metrics for each page
            Map<String, Map<String, Object>> allMetrics = PerformanceUtils.getAllPerformanceMetrics();
            for (Map.Entry<String, Map<String, Object>> entry : allMetrics.entrySet()) {
                String pageName = entry.getKey();
                Map<String, Object> metrics = entry.getValue();
                
                html.append("    <div class=\"page-section\">\n")
                    .append("        <h2 class=\"page-title\">").append(pageName).append("</h2>\n");
                
                // Page load metrics
                html.append("        <h3>Page Load Metrics</h3>\n")
                    .append("        <table>\n")
                    .append("            <tr><th>Metric</th><th>Value</th><th>Status</th></tr>\n");
                
                // Add load time
                if (metrics.containsKey("loadTime")) {
                    long loadTime = (Long) metrics.get("loadTime");
                    String loadTimeClass = loadTime < 1000 ? "metric-good" : (loadTime < 3000 ? "metric-warning" : "metric-bad");
                    html.append("            <tr><td>Load Time</td><td>").append(loadTime).append(" ms</td>")
                        .append("<td class=\"").append(loadTimeClass).append("\">")
                        .append(loadTime < 1000 ? "Good" : (loadTime < 3000 ? "Warning" : "Slow"))
                        .append("</td></tr>\n");
                }
                
                // Add navigation timing metrics
                if (metrics.containsKey("totalTime")) {
                    long totalTime = ((Number) metrics.get("totalTime")).longValue();
                    String totalTimeClass = totalTime < 2000 ? "metric-good" : (totalTime < 5000 ? "metric-warning" : "metric-bad");
                    html.append("            <tr><td>Total Time</td><td>").append(totalTime).append(" ms</td>")
                        .append("<td class=\"").append(totalTimeClass).append("\">")
                        .append(totalTime < 2000 ? "Good" : (totalTime < 5000 ? "Warning" : "Slow"))
                        .append("</td></tr>\n");
                }
                
                if (metrics.containsKey("domLoadTime")) {
                    long domLoadTime = ((Number) metrics.get("domLoadTime")).longValue();
                    String domLoadTimeClass = domLoadTime < 1000 ? "metric-good" : (domLoadTime < 2500 ? "metric-warning" : "metric-bad");
                    html.append("            <tr><td>DOM Load Time</td><td>").append(domLoadTime).append(" ms</td>")
                        .append("<td class=\"").append(domLoadTimeClass).append("\">")
                        .append(domLoadTime < 1000 ? "Good" : (domLoadTime < 2500 ? "Warning" : "Slow"))
                        .append("</td></tr>\n");
                }
                
                html.append("        </table>\n");
                
                // Resource metrics
                if (metrics.containsKey("resources")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resources = (Map<String, Object>) metrics.get("resources");
                    
                    html.append("        <h3>Resource Metrics</h3>\n")
                        .append("        <table>\n")
                        .append("            <tr><th>Metric</th><th>Value</th></tr>\n")
                        .append("            <tr><td>Total Resources</td><td>").append(resources.get("totalResources")).append("</td></tr>\n")
                        .append("            <tr><td>Total Resources Size</td><td>").append(formatBytes(((Number) resources.get("totalResourcesSize")).longValue())).append("</td></tr>\n")
                        .append("        </table>\n");
                    
                    // Resource types
                    if (resources.containsKey("resourcesByType")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Map<String, Object>> resourcesByType = (Map<String, Map<String, Object>>) resources.get("resourcesByType");
                        
                        html.append("        <h3>Resources by Type</h3>\n")
                            .append("        <table>\n")
                            .append("            <tr><th>Type</th><th>Count</th><th>Size</th></tr>\n");
                        
                        for (Map.Entry<String, Map<String, Object>> typeEntry : resourcesByType.entrySet()) {
                            String type = typeEntry.getKey();
                            Map<String, Object> typeData = typeEntry.getValue();
                            
                            html.append("            <tr><td>").append(type).append("</td>")
                                .append("<td>").append(typeData.get("count")).append("</td>")
                                .append("<td>").append(formatBytes(((Number) typeData.get("size")).longValue())).append("</td></tr>\n");
                        }
                        
                        html.append("        </table>\n");
                    }
                    
                    // Slowest resources
                    if (resources.containsKey("slowestResources")) {
                        @SuppressWarnings("unchecked")
                        Object[] slowestResources = ((Object[]) resources.get("slowestResources"));
                        
                        if (slowestResources.length > 0) {
                            html.append("        <h3>Slowest Resources</h3>\n")
                                .append("        <table>\n")
                                .append("            <tr><th>Resource</th><th>Type</th><th>Duration</th><th>Size</th></tr>\n");
                            
                            for (Object resource : slowestResources) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> resourceData = (Map<String, Object>) resource;
                                
                                html.append("            <tr><td>").append(resourceData.get("name")).append("</td>")
                                    .append("<td>").append(resourceData.get("type")).append("</td>")
                                    .append("<td>").append(resourceData.get("duration")).append(" ms</td>")
                                    .append("<td>").append(formatBytes(((Number) resourceData.get("size")).longValue())).append("</td></tr>\n");
                            }
                            
                            html.append("        </table>\n");
                        }
                    }
                }
                
                html.append("    </div>\n");
            }
            
            html.append("</body>\n")
                .append("</html>");
            
            // Write to file
            Files.write(reportFile, html.toString().getBytes());
            
            logger.info("Performance HTML report generated: {}", reportFile);
            return reportFile.toString();
        } catch (IOException e) {
            logger.error("Failed to generate performance HTML report: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Formats bytes into a human-readable format.
     * @param bytes The number of bytes
     * @return A formatted string
     */
    private static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
