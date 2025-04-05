package com.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for measuring and reporting performance metrics.
 * This class provides methods to capture page load times, resource timing, and other performance metrics.
 */
public class PerformanceUtils {

    private static final Logger logger = LogManager.getLogger(PerformanceUtils.class);
    private static final Map<String, Long> pageLoadTimes = new HashMap<>();
    private static final Map<String, Map<String, Object>> performanceMetrics = new HashMap<>();

    /**
     * Starts measuring page load time.
     * @param pageName The name of the page being loaded
     */
    public static void startPageLoadTimer(String pageName) {
        pageLoadTimes.put(pageName, System.currentTimeMillis());
        logger.debug("Started page load timer for: {}", pageName);
    }

    /**
     * Stops measuring page load time and records the result.
     * @param pageName The name of the page being loaded
     * @return The page load time in milliseconds
     */
    public static long stopPageLoadTimer(String pageName) {
        Long startTime = pageLoadTimes.get(pageName);
        if (startTime == null) {
            logger.warn("No start time found for page: {}", pageName);
            return -1;
        }
        
        long loadTime = System.currentTimeMillis() - startTime;
        logger.info("Page load time for {}: {} ms", pageName, loadTime);
        
        // Store in metrics map
        Map<String, Object> metrics = performanceMetrics.getOrDefault(pageName, new HashMap<>());
        metrics.put("loadTime", loadTime);
        performanceMetrics.put(pageName, metrics);
        
        return loadTime;
    }

    /**
     * Captures browser performance metrics using the Navigation Timing API.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return A map of performance metrics
     */
    public static Map<String, Object> capturePerformanceMetrics(WebDriver driver, String pageName) {
        try {
            // Wait for the page to fully load
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").equals("complete"));
            
            // Get performance metrics using Navigation Timing API
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Map<String, Object> timingData = (Map<String, Object>) js.executeScript(
                    "var performance = window.performance || window.webkitPerformance || window.mozPerformance || window.msPerformance || {}; " +
                    "var timings = performance.timing || {}; " +
                    "return { " +
                    "    'navigationStart': timings.navigationStart, " +
                    "    'redirectTime': timings.redirectEnd - timings.redirectStart, " +
                    "    'dnsTime': timings.domainLookupEnd - timings.domainLookupStart, " +
                    "    'connectTime': timings.connectEnd - timings.connectStart, " +
                    "    'responseTime': timings.responseEnd - timings.responseStart, " +
                    "    'domLoadTime': timings.domComplete - timings.domLoading, " +
                    "    'loadEventTime': timings.loadEventEnd - timings.loadEventStart, " +
                    "    'totalTime': timings.loadEventEnd - timings.navigationStart " +
                    "};");
            
            // Log the metrics
            logger.info("Performance metrics for {}: {}", pageName, timingData);
            
            // Store in metrics map
            Map<String, Object> metrics = performanceMetrics.getOrDefault(pageName, new HashMap<>());
            metrics.putAll(timingData);
            performanceMetrics.put(pageName, metrics);
            
            return timingData;
        } catch (Exception e) {
            logger.error("Failed to capture performance metrics: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    /**
     * Captures resource timing information for the page.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return A map of resource timing information
     */
    public static Map<String, Object> captureResourceTiming(WebDriver driver, String pageName) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Map<String, Object> resourceData = (Map<String, Object>) js.executeScript(
                    "var performance = window.performance || window.webkitPerformance || window.mozPerformance || window.msPerformance || {}; " +
                    "var resources = performance.getEntriesByType('resource'); " +
                    "var result = { " +
                    "    'totalResources': resources.length, " +
                    "    'totalResourcesSize': 0, " +
                    "    'slowestResources': [], " +
                    "    'resourcesByType': {} " +
                    "}; " +
                    "resources.forEach(function(resource) { " +
                    "    // Calculate size if available " +
                    "    var size = resource.transferSize || 0; " +
                    "    result.totalResourcesSize += size; " +
                    "    " +
                    "    // Group by initiatorType " +
                    "    var type = resource.initiatorType || 'other'; " +
                    "    if (!result.resourcesByType[type]) { " +
                    "        result.resourcesByType[type] = { count: 0, size: 0 }; " +
                    "    } " +
                    "    result.resourcesByType[type].count++; " +
                    "    result.resourcesByType[type].size += size; " +
                    "    " +
                    "    // Track slow resources " +
                    "    if (resource.duration > 500) { " +
                    "        result.slowestResources.push({ " +
                    "            name: resource.name, " +
                    "            duration: resource.duration, " +
                    "            size: size, " +
                    "            type: type " +
                    "        }); " +
                    "    } " +
                    "}); " +
                    "// Sort slowest resources " +
                    "result.slowestResources.sort(function(a, b) { return b.duration - a.duration; }); " +
                    "// Limit to top 5 " +
                    "result.slowestResources = result.slowestResources.slice(0, 5); " +
                    "return result;");
            
            // Log the resource data
            logger.info("Resource timing for {}: Total resources: {}, Total size: {} bytes", 
                    pageName, resourceData.get("totalResources"), resourceData.get("totalResourcesSize"));
            
            // Store in metrics map
            Map<String, Object> metrics = performanceMetrics.getOrDefault(pageName, new HashMap<>());
            metrics.put("resources", resourceData);
            performanceMetrics.put(pageName, metrics);
            
            return resourceData;
        } catch (Exception e) {
            logger.error("Failed to capture resource timing: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    /**
     * Gets all performance metrics collected during the test run.
     * @return A map of all performance metrics
     */
    public static Map<String, Map<String, Object>> getAllPerformanceMetrics() {
        return performanceMetrics;
    }

    /**
     * Clears all collected performance metrics.
     */
    public static void clearPerformanceMetrics() {
        pageLoadTimes.clear();
        performanceMetrics.clear();
    }

    /**
     * Measures the execution time of a code block.
     * @param operationName The name of the operation being measured
     * @param runnable The code to execute
     */
    public static void measureExecutionTime(String operationName, Runnable runnable) {
        long startTime = System.nanoTime();
        try {
            runnable.run();
        } finally {
            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            logger.info("Execution time for {}: {} ms", operationName, duration);
        }
    }
}
