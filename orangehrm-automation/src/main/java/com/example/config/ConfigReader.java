package com.example.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Reads configuration properties from various sources.
 * This class provides access to configuration properties with environment-specific overrides.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();
    private static final String DEFAULT_ENV = "staging";
    private static String currentEnv;

    static {
        loadDefaultProperties();
        loadEnvironmentProperties();
        loadSystemProperties();
    }

    /**
     * Gets a property value by key.
     * @param key The property key
     * @return The property value or null if not found
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets a property value by key with a default value if not found.
     * @param key The property key
     * @param defaultValue The default value to return if the key is not found
     * @return The property value or the default value if not found
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets the current environment name.
     * @return The current environment name
     */
    public static String getEnvironment() {
        return currentEnv;
    }

    /**
     * Loads the default properties from config.properties.
     */
    private static void loadDefaultProperties() {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(input);
            logger.info("Loaded default properties");
        } catch (IOException e) {
            logger.error("Failed to load default properties", e);
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /**
     * Loads environment-specific properties based on the environment parameter.
     */
    private static void loadEnvironmentProperties() {
        // Get environment from system property or use default
        currentEnv = System.getProperty("environment", DEFAULT_ENV);

        // Try to load environment-specific properties
        Path envPropertiesPath = Paths.get("src/main/resources/environments/", currentEnv + ".properties");

        if (Files.exists(envPropertiesPath)) {
            try (InputStream input = new FileInputStream(envPropertiesPath.toFile())) {
                Properties envProperties = new Properties();
                envProperties.load(input);

                // Override default properties with environment-specific ones
                properties.putAll(envProperties);
                logger.info("Loaded environment properties for: {}", currentEnv);
            } catch (IOException e) {
                logger.error("Failed to load environment properties for: {}", currentEnv, e);
            }
        } else {
            logger.warn("No environment properties file found for: {}", currentEnv);
        }
    }

    /**
     * Loads system properties to override configuration properties.
     */
    private static void loadSystemProperties() {
        // Override with system properties (command line arguments)
        Properties systemProps = System.getProperties();
        for (String key : properties.stringPropertyNames()) {
            String systemValue = systemProps.getProperty(key);
            if (systemValue != null) {
                properties.setProperty(key, systemValue);
                logger.info("Overriding property from system: {} = {}", key, systemValue);
            }
        }
    }
}