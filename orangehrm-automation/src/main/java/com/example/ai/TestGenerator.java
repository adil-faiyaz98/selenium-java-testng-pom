package com.example.ai;

import com.example.config.ConfigReader;
import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI-assisted test generator.
 * This class provides methods to generate test cases based on application behavior.
 */
public class TestGenerator {

    private static final Logger logger = LogManager.getLogger(TestGenerator.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static final String TEST_GEN_DIR = Constants.REPORT_PATH + "generated_tests/";
    
    /**
     * Analyzes a page and generates test cases.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return A list of generated test cases
     */
    public static List<TestCase> generateTestCases(WebDriver driver, String pageName) {
        if (driver == null || pageName == null || pageName.isEmpty()) {
            logger.warn("Invalid driver or page name");
            return new ArrayList<>();
        }
        
        try {
            logger.info("Generating test cases for page: {}", pageName);
            
            List<TestCase> testCases = new ArrayList<>();
            
            // Analyze form fields
            List<TestCase> formTestCases = analyzeFormFields(driver, pageName);
            testCases.addAll(formTestCases);
            
            // Analyze buttons
            List<TestCase> buttonTestCases = analyzeButtons(driver, pageName);
            testCases.addAll(buttonTestCases);
            
            // Analyze links
            List<TestCase> linkTestCases = analyzeLinks(driver, pageName);
            testCases.addAll(linkTestCases);
            
            // Analyze tables
            List<TestCase> tableTestCases = analyzeTables(driver, pageName);
            testCases.addAll(tableTestCases);
            
            // Generate test file
            if (!testCases.isEmpty()) {
                generateTestFile(testCases, pageName);
            }
            
            logger.info("Generated {} test cases for page: {}", testCases.size(), pageName);
            return testCases;
        } catch (Exception e) {
            logger.error("Failed to generate test cases: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Analyzes form fields and generates test cases.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return A list of generated test cases
     */
    private static List<TestCase> analyzeFormFields(WebDriver driver, String pageName) {
        List<TestCase> testCases = new ArrayList<>();
        
        try {
            // Find all input fields
            List<WebElement> inputFields = driver.findElements(By.cssSelector("input, textarea, select"));
            
            for (WebElement field : inputFields) {
                String fieldType = field.getAttribute("type");
                String fieldName = field.getAttribute("name");
                String fieldId = field.getAttribute("id");
                String fieldPlaceholder = field.getAttribute("placeholder");
                
                // Skip hidden fields
                if ("hidden".equals(fieldType)) {
                    continue;
                }
                
                // Determine field name for test case
                String testFieldName = fieldName != null ? fieldName : (fieldId != null ? fieldId : "unknown");
                
                // Determine field label
                String fieldLabel = findFieldLabel(driver, field);
                if (fieldLabel != null && !fieldLabel.isEmpty()) {
                    testFieldName = fieldLabel;
                } else if (fieldPlaceholder != null && !fieldPlaceholder.isEmpty()) {
                    testFieldName = fieldPlaceholder;
                }
                
                // Generate test cases based on field type
                if ("text".equals(fieldType) || "textarea".equals(fieldType) || fieldType == null) {
                    // Text field
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " field accepts valid input",
                            "Enter valid text in the " + testFieldName + " field",
                            "The field should accept the input",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "AcceptsValidInput"
                    ));
                    
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " field validation for empty input",
                            "Leave the " + testFieldName + " field empty and submit the form",
                            "The field should show validation error if required",
                            TestCaseType.NEGATIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "ValidationForEmptyInput"
                    ));
                } else if ("email".equals(fieldType)) {
                    // Email field
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " field accepts valid email",
                            "Enter valid email in the " + testFieldName + " field",
                            "The field should accept the input",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "AcceptsValidEmail"
                    ));
                    
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " field validation for invalid email",
                            "Enter invalid email in the " + testFieldName + " field",
                            "The field should show validation error",
                            TestCaseType.NEGATIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "ValidationForInvalidEmail"
                    ));
                } else if ("password".equals(fieldType)) {
                    // Password field
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " field accepts valid password",
                            "Enter valid password in the " + testFieldName + " field",
                            "The field should accept the input",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "AcceptsValidPassword"
                    ));
                    
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " field validation for weak password",
                            "Enter weak password in the " + testFieldName + " field",
                            "The field should show validation error if password strength is enforced",
                            TestCaseType.NEGATIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "ValidationForWeakPassword"
                    ));
                } else if ("checkbox".equals(fieldType)) {
                    // Checkbox field
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " checkbox can be checked",
                            "Check the " + testFieldName + " checkbox",
                            "The checkbox should be checked",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "CheckboxCanBeChecked"
                    ));
                    
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " checkbox can be unchecked",
                            "Uncheck the " + testFieldName + " checkbox",
                            "The checkbox should be unchecked",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "CheckboxCanBeUnchecked"
                    ));
                } else if ("radio".equals(fieldType)) {
                    // Radio button field
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " radio button can be selected",
                            "Select the " + testFieldName + " radio button",
                            "The radio button should be selected",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "RadioButtonCanBeSelected"
                    ));
                } else if ("select-one".equals(fieldType) || field.getTagName().equalsIgnoreCase("select")) {
                    // Select field
                    testCases.add(new TestCase(
                            "Verify " + testFieldName + " dropdown options can be selected",
                            "Select an option from the " + testFieldName + " dropdown",
                            "The selected option should be displayed",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testFieldName) + "DropdownOptionsCanBeSelected"
                    ));
                }
            }
            
            // Find all forms
            List<WebElement> forms = driver.findElements(By.tagName("form"));
            
            for (int i = 0; i < forms.size(); i++) {
                String formName = "Form " + (i + 1);
                
                testCases.add(new TestCase(
                        "Verify " + formName + " submission with valid data",
                        "Fill all fields with valid data and submit the form",
                        "The form should be submitted successfully",
                        TestCaseType.POSITIVE,
                        pageName + "Test",
                        "test" + sanitizeMethodName(formName) + "SubmissionWithValidData"
                ));
                
                testCases.add(new TestCase(
                        "Verify " + formName + " validation for empty required fields",
                        "Leave required fields empty and submit the form",
                        "The form should show validation errors",
                        TestCaseType.NEGATIVE,
                        pageName + "Test",
                        "test" + sanitizeMethodName(formName) + "ValidationForEmptyRequiredFields"
                ));
            }
        } catch (Exception e) {
            logger.error("Failed to analyze form fields: {}", e.getMessage(), e);
        }
        
        return testCases;
    }

    /**
     * Analyzes buttons and generates test cases.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return A list of generated test cases
     */
    private static List<TestCase> analyzeButtons(WebDriver driver, String pageName) {
        List<TestCase> testCases = new ArrayList<>();
        
        try {
            // Find all buttons
            List<WebElement> buttons = driver.findElements(By.cssSelector("button, input[type='button'], input[type='submit'], .btn"));
            
            for (WebElement button : buttons) {
                String buttonText = button.getText();
                String buttonType = button.getAttribute("type");
                String buttonName = button.getAttribute("name");
                String buttonId = button.getAttribute("id");
                String buttonValue = button.getAttribute("value");
                
                // Determine button name for test case
                String testButtonName = buttonText != null && !buttonText.isEmpty() ? buttonText : 
                        (buttonValue != null && !buttonValue.isEmpty() ? buttonValue : 
                        (buttonName != null ? buttonName : 
                        (buttonId != null ? buttonId : "unknown")));
                
                // Generate test cases based on button type
                if ("submit".equals(buttonType)) {
                    // Submit button
                    testCases.add(new TestCase(
                            "Verify " + testButtonName + " button submits the form",
                            "Fill the form with valid data and click the " + testButtonName + " button",
                            "The form should be submitted successfully",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testButtonName) + "ButtonSubmitsForm"
                    ));
                } else {
                    // Regular button
                    testCases.add(new TestCase(
                            "Verify " + testButtonName + " button functionality",
                            "Click the " + testButtonName + " button",
                            "The button should perform its intended action",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(testButtonName) + "ButtonFunctionality"
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to analyze buttons: {}", e.getMessage(), e);
        }
        
        return testCases;
    }

    /**
     * Analyzes links and generates test cases.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return A list of generated test cases
     */
    private static List<TestCase> analyzeLinks(WebDriver driver, String pageName) {
        List<TestCase> testCases = new ArrayList<>();
        
        try {
            // Find all links
            List<WebElement> links = driver.findElements(By.tagName("a"));
            
            for (WebElement link : links) {
                String linkText = link.getText();
                String linkHref = link.getAttribute("href");
                
                // Skip empty or javascript links
                if (linkText == null || linkText.isEmpty() || linkHref == null || 
                        linkHref.isEmpty() || linkHref.startsWith("javascript:")) {
                    continue;
                }
                
                testCases.add(new TestCase(
                        "Verify " + linkText + " link navigation",
                        "Click the " + linkText + " link",
                        "The link should navigate to the correct page",
                        TestCaseType.POSITIVE,
                        pageName + "Test",
                        "test" + sanitizeMethodName(linkText) + "LinkNavigation"
                ));
            }
        } catch (Exception e) {
            logger.error("Failed to analyze links: {}", e.getMessage(), e);
        }
        
        return testCases;
    }

    /**
     * Analyzes tables and generates test cases.
     * @param driver The WebDriver instance
     * @param pageName The name of the page
     * @return A list of generated test cases
     */
    private static List<TestCase> analyzeTables(WebDriver driver, String pageName) {
        List<TestCase> testCases = new ArrayList<>();
        
        try {
            // Find all tables
            List<WebElement> tables = driver.findElements(By.tagName("table"));
            
            for (int i = 0; i < tables.size(); i++) {
                String tableName = "Table " + (i + 1);
                
                testCases.add(new TestCase(
                        "Verify " + tableName + " data display",
                        "Check the data displayed in the " + tableName,
                        "The table should display the correct data",
                        TestCaseType.POSITIVE,
                        pageName + "Test",
                        "test" + sanitizeMethodName(tableName) + "DataDisplay"
                ));
                
                // Check if table has pagination
                WebElement pagination = null;
                try {
                    pagination = tables.get(i).findElement(By.cssSelector(".pagination, .paging, nav"));
                } catch (Exception e) {
                    // No pagination found
                }
                
                if (pagination != null) {
                    testCases.add(new TestCase(
                            "Verify " + tableName + " pagination",
                            "Navigate through the pages of the " + tableName,
                            "The pagination should work correctly",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(tableName) + "Pagination"
                    ));
                }
                
                // Check if table has sorting
                List<WebElement> sortableHeaders = tables.get(i).findElements(By.cssSelector("th[class*='sort'], th[class*='sortable']"));
                
                if (!sortableHeaders.isEmpty()) {
                    testCases.add(new TestCase(
                            "Verify " + tableName + " sorting",
                            "Click on sortable column headers in the " + tableName,
                            "The table should be sorted correctly",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(tableName) + "Sorting"
                    ));
                }
                
                // Check if table has filtering
                WebElement filter = null;
                try {
                    filter = driver.findElement(By.cssSelector("input[type='search'], .filter-input"));
                } catch (Exception e) {
                    // No filter found
                }
                
                if (filter != null) {
                    testCases.add(new TestCase(
                            "Verify " + tableName + " filtering",
                            "Enter search criteria in the filter input",
                            "The table should display filtered results",
                            TestCaseType.POSITIVE,
                            pageName + "Test",
                            "test" + sanitizeMethodName(tableName) + "Filtering"
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to analyze tables: {}", e.getMessage(), e);
        }
        
        return testCases;
    }

    /**
     * Finds the label for a form field.
     * @param driver The WebDriver instance
     * @param field The form field element
     * @return The label text or null if not found
     */
    private static String findFieldLabel(WebDriver driver, WebElement field) {
        try {
            String fieldId = field.getAttribute("id");
            
            if (fieldId != null && !fieldId.isEmpty()) {
                // Try to find label by for attribute
                WebElement label = driver.findElement(By.cssSelector("label[for='" + fieldId + "']"));
                return label.getText();
            }
            
            // Try to find parent label
            WebElement parentLabel = field.findElement(By.xpath("./ancestor::label"));
            return parentLabel.getText();
        } catch (Exception e) {
            // Label not found
            return null;
        }
    }

    /**
     * Sanitizes a string for use as a method name.
     * @param name The name to sanitize
     * @return The sanitized name
     */
    private static String sanitizeMethodName(String name) {
        if (name == null || name.isEmpty()) {
            return "Unknown";
        }
        
        // Remove special characters and spaces
        String sanitized = name.replaceAll("[^a-zA-Z0-9]", " ");
        
        // Split by whitespace and capitalize each word
        String[] words = sanitized.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
        }
        
        return result.toString();
    }

    /**
     * Generates a test file from test cases.
     * @param testCases The test cases
     * @param pageName The name of the page
     * @return The path to the generated file
     */
    private static String generateTestFile(List<TestCase> testCases, String pageName) {
        try {
            // Create directory if it doesn't exist
            Path testGenDir = Paths.get(TEST_GEN_DIR);
            Files.createDirectories(testGenDir);
            
            // Create test file
            String timestamp = DATE_FORMAT.format(new Date());
            String className = pageName + "Test";
            Path testFile = testGenDir.resolve(className + "_" + timestamp + ".java");
            
            // Group test cases by class name
            Map<String, List<TestCase>> testCasesByClass = testCases.stream()
                    .collect(Collectors.groupingBy(TestCase::getClassName));
            
            // Generate test file content
            StringBuilder content = new StringBuilder();
            content.append("package com.example.tests;\n\n");
            content.append("import com.example.config.ConfigReader;\n");
            content.append("import com.example.pages.").append(pageName).append("Page;\n");
            content.append("import org.testng.Assert;\n");
            content.append("import org.testng.annotations.Test;\n\n");
            content.append("/**\n");
            content.append(" * Generated test class for ").append(pageName).append(" page.\n");
            content.append(" * Generated on: ").append(timestamp).append("\n");
            content.append(" */\n");
            content.append("public class ").append(className).append(" extends BaseTest {\n\n");
            
            // Add test methods
            for (TestCase testCase : testCases) {
                content.append("    /**\n");
                content.append("     * ").append(testCase.getTitle()).append("\n");
                content.append("     * Steps:\n");
                content.append("     * 1. ").append(testCase.getSteps()).append("\n");
                content.append("     * Expected: ").append(testCase.getExpectedResult()).append("\n");
                content.append("     */\n");
                content.append("    @Test(description = \"").append(testCase.getTitle()).append("\")\n");
                content.append("    public void ").append(testCase.getMethodName()).append("() {\n");
                content.append("        logger.info(\"Starting test: ").append(testCase.getMethodName()).append("\");\n");
                content.append("        \n");
                content.append("        // TODO: Implement test steps\n");
                content.append("        // ").append(pageName).append("Page page = new ").append(pageName).append("Page(driver);\n");
                content.append("        \n");
                content.append("        // TODO: Add assertions\n");
                content.append("        // Assert.assertTrue(true, \"Test not implemented\");\n");
                content.append("        \n");
                content.append("        logger.info(\"Finished test: ").append(testCase.getMethodName()).append("\");\n");
                content.append("    }\n\n");
            }
            
            content.append("}\n");
            
            // Write to file
            try (FileWriter writer = new FileWriter(testFile.toFile())) {
                writer.write(content.toString());
            }
            
            logger.info("Generated test file: {}", testFile);
            return testFile.toString();
        } catch (IOException e) {
            logger.error("Failed to generate test file: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Enum representing test case types.
     */
    public enum TestCaseType {
        POSITIVE,
        NEGATIVE,
        BOUNDARY,
        PERFORMANCE,
        SECURITY
    }

    /**
     * Class representing a test case.
     */
    public static class TestCase {
        private final String title;
        private final String steps;
        private final String expectedResult;
        private final TestCaseType type;
        private final String className;
        private final String methodName;
        private final Map<String, Object> metadata = new HashMap<>();

        /**
         * Constructor for TestCase.
         * @param title The test case title
         * @param steps The test steps
         * @param expectedResult The expected result
         * @param type The test case type
         * @param className The class name
         * @param methodName The method name
         */
        public TestCase(String title, String steps, String expectedResult, TestCaseType type, String className, String methodName) {
            this.title = title;
            this.steps = steps;
            this.expectedResult = expectedResult;
            this.type = type;
            this.className = className;
            this.methodName = methodName;
        }

        /**
         * Gets the test case title.
         * @return The title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the test steps.
         * @return The steps
         */
        public String getSteps() {
            return steps;
        }

        /**
         * Gets the expected result.
         * @return The expected result
         */
        public String getExpectedResult() {
            return expectedResult;
        }

        /**
         * Gets the test case type.
         * @return The type
         */
        public TestCaseType getType() {
            return type;
        }

        /**
         * Gets the class name.
         * @return The class name
         */
        public String getClassName() {
            return className;
        }

        /**
         * Gets the method name.
         * @return The method name
         */
        public String getMethodName() {
            return methodName;
        }

        /**
         * Adds metadata to the test case.
         * @param key The metadata key
         * @param value The metadata value
         * @return This TestCase instance for method chaining
         */
        public TestCase addMetadata(String key, Object value) {
            metadata.put(key, value);
            return this;
        }

        /**
         * Gets the metadata.
         * @return The metadata map
         */
        public Map<String, Object> getMetadata() {
            return new HashMap<>(metadata);
        }

        @Override
        public String toString() {
            return String.format("TestCase{title='%s', type=%s, className='%s', methodName='%s'}", 
                    title, type, className, methodName);
        }
    }
}
