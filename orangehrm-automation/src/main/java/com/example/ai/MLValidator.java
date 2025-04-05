package com.example.ai;

import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Simple machine learning-based validator for form fields and content.
 * This class provides methods to validate form inputs and content using simple ML techniques.
 */
public class MLValidator {

    private static final Logger logger = LogManager.getLogger(MLValidator.class);
    private static final String MODEL_DIR = Constants.REPORT_PATH + "ml/models/";
    private static final Map<String, List<String>> trainingData = new HashMap<>();
    private static final Map<String, Pattern> patterns = new HashMap<>();

    /**
     * Initializes the ML validator.
     */
    public static void initialize() {
        try {
            // Create model directory if it doesn't exist
            Path modelDir = Paths.get(MODEL_DIR);
            Files.createDirectories(modelDir);
            
            // Load existing models
            loadModels();
            
            logger.info("ML validator initialized");
        } catch (IOException e) {
            logger.error("Failed to initialize ML validator: {}", e.getMessage(), e);
        }
    }

    /**
     * Loads existing models from the model directory.
     */
    private static void loadModels() {
        try {
            Path modelDir = Paths.get(MODEL_DIR);
            if (!Files.exists(modelDir)) {
                return;
            }
            
            Files.list(modelDir)
                .filter(path -> path.toString().endsWith(".model"))
                .forEach(path -> {
                    String fieldType = path.getFileName().toString().replace(".model", "");
                    try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                        List<String> examples = new ArrayList<>();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            examples.add(line);
                        }
                        trainingData.put(fieldType, examples);
                        
                        // Generate pattern
                        generatePattern(fieldType);
                        
                        logger.info("Loaded model for field type: {} with {} examples", fieldType, examples.size());
                    } catch (IOException e) {
                        logger.error("Failed to load model for field type {}: {}", fieldType, e.getMessage(), e);
                    }
                });
        } catch (IOException e) {
            logger.error("Failed to load models: {}", e.getMessage(), e);
        }
    }

    /**
     * Generates a regex pattern for a field type based on training data.
     * @param fieldType The field type
     */
    private static void generatePattern(String fieldType) {
        List<String> examples = trainingData.get(fieldType);
        if (examples == null || examples.isEmpty()) {
            return;
        }
        
        switch (fieldType) {
            case "email":
                patterns.put(fieldType, Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"));
                break;
            case "phone":
                patterns.put(fieldType, Pattern.compile("^\\+?[0-9\\s-()]{7,}$"));
                break;
            case "date":
                patterns.put(fieldType, Pattern.compile("^\\d{1,4}[/-]\\d{1,2}[/-]\\d{1,4}$"));
                break;
            case "name":
                patterns.put(fieldType, Pattern.compile("^[a-zA-Z\\s'-]{2,}$"));
                break;
            case "address":
                patterns.put(fieldType, Pattern.compile("^[a-zA-Z0-9\\s,.-]{5,}$"));
                break;
            case "zipcode":
                patterns.put(fieldType, Pattern.compile("^\\d{5}(-\\d{4})?$"));
                break;
            default:
                // For custom field types, generate a pattern based on common characters in examples
                String commonChars = examples.stream()
                        .flatMapToInt(String::chars)
                        .mapToObj(c -> (char) c)
                        .filter(c -> examples.stream().allMatch(ex -> ex.indexOf(c) >= 0))
                        .map(String::valueOf)
                        .collect(Collectors.joining());
                
                if (!commonChars.isEmpty()) {
                    patterns.put(fieldType, Pattern.compile("^[" + Pattern.quote(commonChars) + "\\w\\s]{1,}$"));
                } else {
                    patterns.put(fieldType, Pattern.compile("^.+$")); // Default pattern
                }
                break;
        }
        
        logger.info("Generated pattern for field type: {}", fieldType);
    }

    /**
     * Trains the validator with examples for a field type.
     * @param fieldType The field type
     * @param examples The examples
     * @return true if training was successful, false otherwise
     */
    public static boolean train(String fieldType, List<String> examples) {
        if (fieldType == null || fieldType.isEmpty() || examples == null || examples.isEmpty()) {
            logger.warn("Invalid training data for field type: {}", fieldType);
            return false;
        }
        
        try {
            // Add examples to training data
            List<String> existingExamples = trainingData.getOrDefault(fieldType, new ArrayList<>());
            existingExamples.addAll(examples);
            trainingData.put(fieldType, existingExamples);
            
            // Generate pattern
            generatePattern(fieldType);
            
            // Save model
            Path modelFile = Paths.get(MODEL_DIR, fieldType + ".model");
            try (FileWriter writer = new FileWriter(modelFile.toFile())) {
                for (String example : existingExamples) {
                    writer.write(example + "\n");
                }
            }
            
            logger.info("Trained model for field type: {} with {} examples", fieldType, existingExamples.size());
            return true;
        } catch (IOException e) {
            logger.error("Failed to train model for field type {}: {}", fieldType, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Validates a form field value against a field type.
     * @param fieldType The field type
     * @param value The value to validate
     * @return A validation result
     */
    public static ValidationResult validateField(String fieldType, String value) {
        if (fieldType == null || fieldType.isEmpty() || value == null) {
            return new ValidationResult(false, "Invalid field type or value");
        }
        
        // Check if we have a pattern for this field type
        Pattern pattern = patterns.get(fieldType);
        if (pattern == null) {
            logger.warn("No pattern found for field type: {}", fieldType);
            return new ValidationResult(false, "No pattern found for field type: " + fieldType);
        }
        
        // Validate using pattern
        boolean matches = pattern.matcher(value).matches();
        
        // Check against training examples for similarity
        List<String> examples = trainingData.get(fieldType);
        if (examples != null && !examples.isEmpty()) {
            // Find the most similar example
            String mostSimilar = examples.stream()
                    .max((a, b) -> Double.compare(calculateSimilarity(value, a), calculateSimilarity(value, b)))
                    .orElse("");
            
            double similarity = calculateSimilarity(value, mostSimilar);
            
            if (similarity > 0.8) { // High similarity threshold
                matches = true;
            } else if (similarity > 0.5 && matches) { // Medium similarity, pattern already matched
                matches = true;
            } else if (similarity < 0.3 && matches) { // Low similarity, but pattern matched
                matches = false;
            }
            
            logger.debug("Field validation: type={}, value={}, pattern_match={}, similarity={}, most_similar={}",
                    fieldType, value, pattern.matcher(value).matches(), similarity, mostSimilar);
            
            return new ValidationResult(matches, 
                    matches ? "Valid " + fieldType : "Invalid " + fieldType, 
                    similarity);
        }
        
        logger.debug("Field validation: type={}, value={}, pattern_match={}", 
                fieldType, value, matches);
        
        return new ValidationResult(matches, 
                matches ? "Valid " + fieldType : "Invalid " + fieldType);
    }

    /**
     * Validates a form field element against a field type.
     * @param element The WebElement to validate
     * @param fieldType The field type
     * @return A validation result
     */
    public static ValidationResult validateField(WebElement element, String fieldType) {
        if (element == null) {
            return new ValidationResult(false, "Element is null");
        }
        
        String value = element.getAttribute("value");
        return validateField(fieldType, value);
    }

    /**
     * Validates text content against expected content type.
     * @param content The content to validate
     * @param contentType The expected content type
     * @return A validation result
     */
    public static ValidationResult validateContent(String content, String contentType) {
        if (content == null || content.isEmpty() || contentType == null || contentType.isEmpty()) {
            return new ValidationResult(false, "Invalid content or content type");
        }
        
        switch (contentType.toLowerCase()) {
            case "error_message":
                // Check if it looks like an error message
                boolean isError = content.toLowerCase().contains("error") || 
                        content.toLowerCase().contains("invalid") ||
                        content.toLowerCase().contains("failed") ||
                        content.toLowerCase().contains("not found") ||
                        content.toLowerCase().contains("required");
                
                return new ValidationResult(isError, 
                        isError ? "Valid error message" : "Not an error message");
                
            case "success_message":
                // Check if it looks like a success message
                boolean isSuccess = content.toLowerCase().contains("success") || 
                        content.toLowerCase().contains("completed") ||
                        content.toLowerCase().contains("saved") ||
                        content.toLowerCase().contains("updated") ||
                        content.toLowerCase().contains("created");
                
                return new ValidationResult(isSuccess, 
                        isSuccess ? "Valid success message" : "Not a success message");
                
            case "heading":
                // Check if it looks like a heading (short, no punctuation at end)
                boolean isHeading = content.length() < 100 && 
                        !content.endsWith(".") && 
                        !content.endsWith("!") && 
                        !content.endsWith("?");
                
                return new ValidationResult(isHeading, 
                        isHeading ? "Valid heading" : "Not a heading");
                
            case "paragraph":
                // Check if it looks like a paragraph (longer, has sentences)
                boolean isParagraph = content.length() > 50 && 
                        content.contains(". ");
                
                return new ValidationResult(isParagraph, 
                        isParagraph ? "Valid paragraph" : "Not a paragraph");
                
            default:
                // For custom content types, check against training data
                List<String> examples = trainingData.get(contentType);
                if (examples != null && !examples.isEmpty()) {
                    // Find the most similar example
                    String mostSimilar = examples.stream()
                            .max((a, b) -> Double.compare(calculateSimilarity(content, a), calculateSimilarity(content, b)))
                            .orElse("");
                    
                    double similarity = calculateSimilarity(content, mostSimilar);
                    boolean isValid = similarity > 0.6; // Threshold for content similarity
                    
                    return new ValidationResult(isValid, 
                            isValid ? "Valid " + contentType : "Invalid " + contentType, 
                            similarity);
                }
                
                return new ValidationResult(false, "Unknown content type: " + contentType);
        }
    }

    /**
     * Calculates the similarity between two strings using Levenshtein distance.
     * @param str1 The first string
     * @param str2 The second string
     * @return A similarity score between 0.0 and 1.0
     */
    private static double calculateSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0.0;
        }
        
        // Levenshtein distance
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];
        
        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
        
        for (int j = 0; j <= str2.length(); j++) {
            distance[0][j] = j;
        }
        
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
                distance[i][j] = Math.min(
                        Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + cost);
            }
        }
        
        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) {
            return 1.0; // Both strings are empty
        }
        
        return 1.0 - ((double) distance[str1.length()][str2.length()] / maxLength);
    }

    /**
     * Class representing the result of a validation.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final double confidence;

        /**
         * Constructor for ValidationResult.
         * @param valid Whether the validation passed
         * @param message The validation message
         */
        public ValidationResult(boolean valid, String message) {
            this(valid, message, valid ? 1.0 : 0.0);
        }

        /**
         * Constructor for ValidationResult with confidence.
         * @param valid Whether the validation passed
         * @param message The validation message
         * @param confidence The confidence level (0.0 to 1.0)
         */
        public ValidationResult(boolean valid, String message, double confidence) {
            this.valid = valid;
            this.message = message;
            this.confidence = confidence;
        }

        /**
         * Gets whether the validation passed.
         * @return true if the validation passed, false otherwise
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Gets the validation message.
         * @return The validation message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the confidence level.
         * @return The confidence level (0.0 to 1.0)
         */
        public double getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, confidence=%.2f, message='%s'}", 
                    valid, confidence, message);
        }
    }

    // Initialize the validator when the class is loaded
    static {
        initialize();
    }
}
