package com.example.utils;

import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Reporter class for generating visual testing reports.
 * This class provides methods to generate HTML reports for visual comparison results.
 */
public class VisualTestingReporter {

    private static final Logger logger = LogManager.getLogger(VisualTestingReporter.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static final List<VisualComparisonUtils.ComparisonResult> results = new ArrayList<>();

    /**
     * Adds a comparison result to the report.
     * @param result The comparison result to add
     */
    public static void addResult(VisualComparisonUtils.ComparisonResult result) {
        results.add(result);
    }

    /**
     * Generates an HTML report for all comparison results.
     * @param testName The name of the test
     * @return The path to the generated report
     */
    public static String generateHtmlReport(String testName) {
        try {
            // Create directory if it doesn't exist
            Path reportDir = Paths.get(Constants.REPORT_PATH, "visual");
            Files.createDirectories(reportDir);
            
            // Create report file
            String timestamp = DATE_FORMAT.format(new Date());
            Path reportFile = reportDir.resolve(testName + "_visual_report_" + timestamp + ".html");
            
            // Generate HTML content
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("    <title>Visual Testing Report - ").append(testName).append("</title>\n")
                .append("    <style>\n")
                .append("        body { font-family: Arial, sans-serif; margin: 20px; }\n")
                .append("        h1 { color: #333; }\n")
                .append("        .summary { margin-bottom: 20px; }\n")
                .append("        .result { margin-bottom: 30px; border: 1px solid #ddd; padding: 15px; border-radius: 5px; }\n")
                .append("        .result-header { display: flex; justify-content: space-between; align-items: center; }\n")
                .append("        .result-title { margin-top: 0; }\n")
                .append("        .passed { color: green; }\n")
                .append("        .failed { color: red; }\n")
                .append("        .images { display: flex; flex-wrap: wrap; gap: 20px; margin-top: 15px; }\n")
                .append("        .image-container { text-align: center; }\n")
                .append("        .image-container img { max-width: 300px; max-height: 300px; border: 1px solid #ddd; }\n")
                .append("        table { border-collapse: collapse; width: 100%; margin-top: 15px; }\n")
                .append("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n")
                .append("        th { background-color: #f2f2f2; }\n")
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <h1>Visual Testing Report - ").append(testName).append("</h1>\n")
                .append("    <p>Generated on: ").append(timestamp).append("</p>\n");
            
            // Add summary
            int totalTests = results.size();
            int passedTests = (int) results.stream().filter(VisualComparisonUtils.ComparisonResult::isPassed).count();
            int failedTests = totalTests - passedTests;
            
            html.append("    <div class=\"summary\">\n")
                .append("        <h2>Summary</h2>\n")
                .append("        <p>Total Tests: ").append(totalTests).append("</p>\n")
                .append("        <p>Passed: <span class=\"passed\">").append(passedTests).append("</span></p>\n")
                .append("        <p>Failed: <span class=\"failed\">").append(failedTests).append("</span></p>\n")
                .append("    </div>\n");
            
            // Add results
            for (int i = 0; i < results.size(); i++) {
                VisualComparisonUtils.ComparisonResult result = results.get(i);
                String resultId = "result-" + (i + 1);
                String resultClass = result.isPassed() ? "passed" : "failed";
                String resultText = result.isPassed() ? "PASSED" : "FAILED";
                
                html.append("    <div id=\"").append(resultId).append("\" class=\"result\">\n")
                    .append("        <div class=\"result-header\">\n")
                    .append("            <h2 class=\"result-title\">Comparison #").append(i + 1).append("</h2>\n")
                    .append("            <h3 class=\"").append(resultClass).append("\">").append(resultText).append("</h3>\n")
                    .append("        </div>\n")
                    .append("        <p>Difference: ").append(String.format("%.2f%%", result.getDiffPercentage() * 100)).append("</p>\n");
                
                // Add metadata if available
                if (!result.getMetadata().isEmpty()) {
                    html.append("        <table>\n")
                        .append("            <tr><th>Metadata</th><th>Value</th></tr>\n");
                    
                    for (var entry : result.getMetadata().entrySet()) {
                        html.append("            <tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>\n");
                    }
                    
                    html.append("        </table>\n");
                }
                
                // Add images
                html.append("        <div class=\"images\">\n");
                
                // Baseline image
                if (result.getBaselinePath() != null) {
                    String baselineRelativePath = getRelativePath(reportFile.getParent(), Paths.get(result.getBaselinePath()));
                    html.append("            <div class=\"image-container\">\n")
                        .append("                <h4>Baseline</h4>\n")
                        .append("                <img src=\"").append(baselineRelativePath).append("\" alt=\"Baseline Image\">\n")
                        .append("            </div>\n");
                }
                
                // Actual image
                if (result.getActualPath() != null) {
                    String actualRelativePath = getRelativePath(reportFile.getParent(), Paths.get(result.getActualPath()));
                    html.append("            <div class=\"image-container\">\n")
                        .append("                <h4>Actual</h4>\n")
                        .append("                <img src=\"").append(actualRelativePath).append("\" alt=\"Actual Image\">\n")
                        .append("            </div>\n");
                }
                
                // Diff image
                if (result.getDiffPath() != null) {
                    String diffRelativePath = getRelativePath(reportFile.getParent(), Paths.get(result.getDiffPath()));
                    html.append("            <div class=\"image-container\">\n")
                        .append("                <h4>Difference</h4>\n")
                        .append("                <img src=\"").append(diffRelativePath).append("\" alt=\"Difference Image\">\n")
                        .append("            </div>\n");
                }
                
                html.append("        </div>\n")
                    .append("    </div>\n");
            }
            
            html.append("</body>\n")
                .append("</html>");
            
            // Write to file
            try (FileWriter writer = new FileWriter(reportFile.toFile())) {
                writer.write(html.toString());
            }
            
            logger.info("Visual testing HTML report generated: {}", reportFile);
            return reportFile.toString();
        } catch (IOException e) {
            logger.error("Failed to generate visual testing HTML report: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets the relative path from one path to another.
     * @param from The source path
     * @param to The target path
     * @return The relative path
     */
    private static String getRelativePath(Path from, Path to) {
        try {
            return from.relativize(to).toString().replace('\\', '/');
        } catch (Exception e) {
            logger.warn("Failed to get relative path from {} to {}: {}", from, to, e.getMessage());
            return to.toString().replace('\\', '/');
        }
    }

    /**
     * Clears all comparison results.
     */
    public static void clearResults() {
        results.clear();
    }
}
