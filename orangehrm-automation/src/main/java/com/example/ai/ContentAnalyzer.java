package com.example.ai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Content analyzer that uses NLP techniques to analyze page content.
 * This class provides methods to analyze text content for sentiment, relevance, and consistency.
 */
public class ContentAnalyzer {

    private static final Logger logger = LogManager.getLogger(ContentAnalyzer.class);
    
    // Common stop words to ignore in analysis
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "if", "then", "else", "when",
            "at", "by", "for", "with", "about", "against", "between", "into",
            "through", "during", "before", "after", "above", "below", "to",
            "from", "up", "down", "in", "out", "on", "off", "over", "under",
            "again", "further", "then", "once", "here", "there", "when", "where",
            "why", "how", "all", "any", "both", "each", "few", "more", "most",
            "other", "some", "such", "no", "nor", "not", "only", "own", "same",
            "so", "than", "too", "very", "s", "t", "can", "will", "just", "don",
            "should", "now", "d", "ll", "m", "o", "re", "ve", "y", "ain", "aren",
            "couldn", "didn", "doesn", "hadn", "hasn", "haven", "isn", "ma",
            "mightn", "mustn", "needn", "shan", "shouldn", "wasn", "weren", "won",
            "wouldn", "of", "is", "are", "am", "was", "were", "be", "been", "being"
    ));
    
    // Positive sentiment words
    private static final Set<String> POSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "good", "great", "excellent", "amazing", "awesome", "nice", "wonderful",
            "fantastic", "terrific", "outstanding", "superb", "brilliant", "perfect",
            "success", "successful", "completed", "approved", "confirmed", "valid",
            "correct", "right", "yes", "ok", "okay", "saved", "created", "added",
            "updated", "modified", "deleted", "removed", "welcome", "congratulations"
    ));
    
    // Negative sentiment words
    private static final Set<String> NEGATIVE_WORDS = new HashSet<>(Arrays.asList(
            "bad", "poor", "terrible", "awful", "horrible", "wrong", "error",
            "fail", "failed", "failure", "invalid", "incorrect", "not", "no",
            "denied", "rejected", "unauthorized", "forbidden", "missing", "required",
            "empty", "blank", "null", "undefined", "exception", "error", "warning",
            "alert", "danger", "critical", "severe", "fatal", "sorry"
    ));

    /**
     * Analyzes the sentiment of text content.
     * @param content The text content to analyze
     * @return A sentiment analysis result
     */
    public static SentimentResult analyzeSentiment(String content) {
        if (content == null || content.isEmpty()) {
            return new SentimentResult(SentimentType.NEUTRAL, 0.0, "Empty content");
        }
        
        // Tokenize and normalize
        List<String> tokens = tokenize(content);
        
        // Count positive and negative words
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String token : tokens) {
            if (POSITIVE_WORDS.contains(token)) {
                positiveCount++;
            } else if (NEGATIVE_WORDS.contains(token)) {
                negativeCount++;
            }
        }
        
        // Calculate sentiment score (-1.0 to 1.0)
        double score = 0.0;
        if (tokens.size() > 0) {
            score = (double) (positiveCount - negativeCount) / tokens.size();
        }
        
        // Determine sentiment type
        SentimentType type;
        String explanation;
        
        if (score > 0.1) {
            type = SentimentType.POSITIVE;
            explanation = "Positive sentiment detected with score " + String.format("%.2f", score);
        } else if (score < -0.1) {
            type = SentimentType.NEGATIVE;
            explanation = "Negative sentiment detected with score " + String.format("%.2f", score);
        } else {
            type = SentimentType.NEUTRAL;
            explanation = "Neutral sentiment detected with score " + String.format("%.2f", score);
        }
        
        logger.debug("Sentiment analysis: content='{}', score={}, type={}", 
                content.length() > 50 ? content.substring(0, 50) + "..." : content, 
                score, type);
        
        return new SentimentResult(type, score, explanation);
    }

    /**
     * Analyzes the relevance of text content to a topic.
     * @param content The text content to analyze
     * @param topic The topic to check relevance against
     * @return A relevance analysis result
     */
    public static RelevanceResult analyzeRelevance(String content, String topic) {
        if (content == null || content.isEmpty() || topic == null || topic.isEmpty()) {
            return new RelevanceResult(0.0, "Empty content or topic");
        }
        
        // Tokenize and normalize
        List<String> contentTokens = tokenize(content);
        List<String> topicTokens = tokenize(topic);
        
        // Remove stop words
        contentTokens = contentTokens.stream()
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toList());
        
        topicTokens = topicTokens.stream()
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toList());
        
        // Calculate term frequency
        Map<String, Integer> contentTF = calculateTermFrequency(contentTokens);
        Map<String, Integer> topicTF = calculateTermFrequency(topicTokens);
        
        // Calculate cosine similarity
        double similarity = calculateCosineSimilarity(contentTF, topicTF);
        
        String explanation;
        if (similarity > 0.7) {
            explanation = "High relevance to topic '" + topic + "'";
        } else if (similarity > 0.4) {
            explanation = "Moderate relevance to topic '" + topic + "'";
        } else {
            explanation = "Low relevance to topic '" + topic + "'";
        }
        
        logger.debug("Relevance analysis: content='{}', topic='{}', similarity={}", 
                content.length() > 50 ? content.substring(0, 50) + "..." : content, 
                topic, similarity);
        
        return new RelevanceResult(similarity, explanation);
    }

    /**
     * Analyzes the consistency of text content across multiple elements.
     * @param elements The WebElements containing text to analyze
     * @return A consistency analysis result
     */
    public static ConsistencyResult analyzeConsistency(List<WebElement> elements) {
        if (elements == null || elements.isEmpty()) {
            return new ConsistencyResult(0.0, "No elements provided");
        }
        
        // Extract text from elements
        List<String> texts = elements.stream()
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isEmpty())
                .collect(Collectors.toList());
        
        if (texts.isEmpty()) {
            return new ConsistencyResult(0.0, "No text found in elements");
        }
        
        // Tokenize and normalize
        List<List<String>> tokenizedTexts = texts.stream()
                .map(ContentAnalyzer::tokenize)
                .collect(Collectors.toList());
        
        // Calculate term frequency for each text
        List<Map<String, Integer>> termFrequencies = tokenizedTexts.stream()
                .map(ContentAnalyzer::calculateTermFrequency)
                .collect(Collectors.toList());
        
        // Calculate average pairwise similarity
        double totalSimilarity = 0.0;
        int pairCount = 0;
        
        for (int i = 0; i < termFrequencies.size(); i++) {
            for (int j = i + 1; j < termFrequencies.size(); j++) {
                double similarity = calculateCosineSimilarity(termFrequencies.get(i), termFrequencies.get(j));
                totalSimilarity += similarity;
                pairCount++;
            }
        }
        
        double averageSimilarity = pairCount > 0 ? totalSimilarity / pairCount : 0.0;
        
        String explanation;
        if (averageSimilarity > 0.8) {
            explanation = "High consistency across elements";
        } else if (averageSimilarity > 0.5) {
            explanation = "Moderate consistency across elements";
        } else {
            explanation = "Low consistency across elements";
        }
        
        logger.debug("Consistency analysis: elements={}, average_similarity={}", 
                elements.size(), averageSimilarity);
        
        return new ConsistencyResult(averageSimilarity, explanation);
    }

    /**
     * Analyzes a page for content relevance to a topic.
     * @param driver The WebDriver instance
     * @param topic The topic to check relevance against
     * @return A page analysis result
     */
    public static PageAnalysisResult analyzePage(WebDriver driver, String topic) {
        if (driver == null || topic == null || topic.isEmpty()) {
            return new PageAnalysisResult(0.0, "Invalid driver or topic");
        }
        
        try {
            // Extract text from various elements
            List<WebElement> headings = driver.findElements(By.cssSelector("h1, h2, h3, h4, h5, h6"));
            List<WebElement> paragraphs = driver.findElements(By.tagName("p"));
            List<WebElement> links = driver.findElements(By.tagName("a"));
            
            // Combine all text
            StringBuilder allText = new StringBuilder();
            
            // Add page title
            allText.append(driver.getTitle()).append(" ");
            
            // Add headings (with higher weight)
            for (WebElement heading : headings) {
                String text = heading.getText();
                if (text != null && !text.isEmpty()) {
                    allText.append(text).append(" ").append(text).append(" "); // Repeat for higher weight
                }
            }
            
            // Add paragraphs
            for (WebElement paragraph : paragraphs) {
                String text = paragraph.getText();
                if (text != null && !text.isEmpty()) {
                    allText.append(text).append(" ");
                }
            }
            
            // Add link text
            for (WebElement link : links) {
                String text = link.getText();
                if (text != null && !text.isEmpty()) {
                    allText.append(text).append(" ");
                }
            }
            
            // Analyze relevance
            RelevanceResult relevance = analyzeRelevance(allText.toString(), topic);
            
            // Analyze sentiment
            SentimentResult sentiment = analyzeSentiment(allText.toString());
            
            // Create result
            PageAnalysisResult result = new PageAnalysisResult(relevance.getScore(), 
                    "Page analysis for topic '" + topic + "'");
            
            result.setRelevance(relevance);
            result.setSentiment(sentiment);
            
            // Add element-specific analysis
            if (!headings.isEmpty()) {
                String headingsText = headings.stream()
                        .map(WebElement::getText)
                        .filter(text -> text != null && !text.isEmpty())
                        .collect(Collectors.joining(" "));
                
                result.addElementAnalysis("headings", analyzeRelevance(headingsText, topic));
            }
            
            if (!paragraphs.isEmpty()) {
                String paragraphsText = paragraphs.stream()
                        .map(WebElement::getText)
                        .filter(text -> text != null && !text.isEmpty())
                        .collect(Collectors.joining(" "));
                
                result.addElementAnalysis("paragraphs", analyzeRelevance(paragraphsText, topic));
            }
            
            logger.info("Page analysis: url='{}', topic='{}', relevance={}, sentiment={}", 
                    driver.getCurrentUrl(), topic, relevance.getScore(), sentiment.getType());
            
            return result;
        } catch (Exception e) {
            logger.error("Failed to analyze page: {}", e.getMessage(), e);
            return new PageAnalysisResult(0.0, "Error analyzing page: " + e.getMessage());
        }
    }

    /**
     * Tokenizes text into words.
     * @param text The text to tokenize
     * @return A list of tokens
     */
    private static List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Convert to lowercase
        text = text.toLowerCase();
        
        // Remove punctuation and split by whitespace
        text = text.replaceAll("[^a-zA-Z0-9\\s]", " ");
        
        return Arrays.stream(text.split("\\s+"))
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Calculates term frequency for a list of tokens.
     * @param tokens The tokens
     * @return A map of term frequencies
     */
    private static Map<String, Integer> calculateTermFrequency(List<String> tokens) {
        Map<String, Integer> tf = new HashMap<>();
        
        for (String token : tokens) {
            tf.put(token, tf.getOrDefault(token, 0) + 1);
        }
        
        return tf;
    }

    /**
     * Calculates cosine similarity between two term frequency maps.
     * @param tf1 The first term frequency map
     * @param tf2 The second term frequency map
     * @return The cosine similarity
     */
    private static double calculateCosineSimilarity(Map<String, Integer> tf1, Map<String, Integer> tf2) {
        if (tf1.isEmpty() || tf2.isEmpty()) {
            return 0.0;
        }
        
        // Calculate dot product
        double dotProduct = 0.0;
        for (Map.Entry<String, Integer> entry : tf1.entrySet()) {
            String term = entry.getKey();
            if (tf2.containsKey(term)) {
                dotProduct += entry.getValue() * tf2.get(term);
            }
        }
        
        // Calculate magnitudes
        double magnitude1 = Math.sqrt(tf1.values().stream().mapToDouble(count -> count * count).sum());
        double magnitude2 = Math.sqrt(tf2.values().stream().mapToDouble(count -> count * count).sum());
        
        // Calculate similarity
        if (magnitude1 > 0 && magnitude2 > 0) {
            return dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }
    }

    /**
     * Enum representing sentiment types.
     */
    public enum SentimentType {
        POSITIVE,
        NEUTRAL,
        NEGATIVE
    }

    /**
     * Class representing a sentiment analysis result.
     */
    public static class SentimentResult {
        private final SentimentType type;
        private final double score;
        private final String explanation;

        /**
         * Constructor for SentimentResult.
         * @param type The sentiment type
         * @param score The sentiment score
         * @param explanation The explanation
         */
        public SentimentResult(SentimentType type, double score, String explanation) {
            this.type = type;
            this.score = score;
            this.explanation = explanation;
        }

        /**
         * Gets the sentiment type.
         * @return The sentiment type
         */
        public SentimentType getType() {
            return type;
        }

        /**
         * Gets the sentiment score.
         * @return The sentiment score
         */
        public double getScore() {
            return score;
        }

        /**
         * Gets the explanation.
         * @return The explanation
         */
        public String getExplanation() {
            return explanation;
        }

        @Override
        public String toString() {
            return String.format("SentimentResult{type=%s, score=%.2f, explanation='%s'}", 
                    type, score, explanation);
        }
    }

    /**
     * Class representing a relevance analysis result.
     */
    public static class RelevanceResult {
        private final double score;
        private final String explanation;

        /**
         * Constructor for RelevanceResult.
         * @param score The relevance score
         * @param explanation The explanation
         */
        public RelevanceResult(double score, String explanation) {
            this.score = score;
            this.explanation = explanation;
        }

        /**
         * Gets the relevance score.
         * @return The relevance score
         */
        public double getScore() {
            return score;
        }

        /**
         * Gets the explanation.
         * @return The explanation
         */
        public String getExplanation() {
            return explanation;
        }

        @Override
        public String toString() {
            return String.format("RelevanceResult{score=%.2f, explanation='%s'}", 
                    score, explanation);
        }
    }

    /**
     * Class representing a consistency analysis result.
     */
    public static class ConsistencyResult {
        private final double score;
        private final String explanation;

        /**
         * Constructor for ConsistencyResult.
         * @param score The consistency score
         * @param explanation The explanation
         */
        public ConsistencyResult(double score, String explanation) {
            this.score = score;
            this.explanation = explanation;
        }

        /**
         * Gets the consistency score.
         * @return The consistency score
         */
        public double getScore() {
            return score;
        }

        /**
         * Gets the explanation.
         * @return The explanation
         */
        public String getExplanation() {
            return explanation;
        }

        @Override
        public String toString() {
            return String.format("ConsistencyResult{score=%.2f, explanation='%s'}", 
                    score, explanation);
        }
    }

    /**
     * Class representing a page analysis result.
     */
    public static class PageAnalysisResult {
        private final double score;
        private final String explanation;
        private SentimentResult sentiment;
        private RelevanceResult relevance;
        private final Map<String, RelevanceResult> elementAnalysis = new HashMap<>();

        /**
         * Constructor for PageAnalysisResult.
         * @param score The overall score
         * @param explanation The explanation
         */
        public PageAnalysisResult(double score, String explanation) {
            this.score = score;
            this.explanation = explanation;
        }

        /**
         * Gets the overall score.
         * @return The overall score
         */
        public double getScore() {
            return score;
        }

        /**
         * Gets the explanation.
         * @return The explanation
         */
        public String getExplanation() {
            return explanation;
        }

        /**
         * Gets the sentiment result.
         * @return The sentiment result
         */
        public SentimentResult getSentiment() {
            return sentiment;
        }

        /**
         * Sets the sentiment result.
         * @param sentiment The sentiment result
         */
        public void setSentiment(SentimentResult sentiment) {
            this.sentiment = sentiment;
        }

        /**
         * Gets the relevance result.
         * @return The relevance result
         */
        public RelevanceResult getRelevance() {
            return relevance;
        }

        /**
         * Sets the relevance result.
         * @param relevance The relevance result
         */
        public void setRelevance(RelevanceResult relevance) {
            this.relevance = relevance;
        }

        /**
         * Adds an element analysis result.
         * @param elementType The element type
         * @param result The relevance result
         */
        public void addElementAnalysis(String elementType, RelevanceResult result) {
            elementAnalysis.put(elementType, result);
        }

        /**
         * Gets the element analysis results.
         * @return The element analysis results
         */
        public Map<String, RelevanceResult> getElementAnalysis() {
            return new HashMap<>(elementAnalysis);
        }

        @Override
        public String toString() {
            return String.format("PageAnalysisResult{score=%.2f, explanation='%s', sentiment=%s, relevance=%s, elementAnalysis=%s}", 
                    score, explanation, sentiment, relevance, elementAnalysis);
        }
    }
}
