package com.example.utils;

import com.example.config.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling test data.
 * This class provides methods for reading data from various file formats.
 */
public class DataUtils {

    private static final Logger logger = LogManager.getLogger(DataUtils.class);

    /**
     * Reads data from a CSV file.
     * @param fileName The name of the CSV file (without path)
     * @return A list of string arrays, each representing a row in the CSV
     */
    public static List<String[]> readDataFromCsv(String fileName) {
        String filePath = Constants.TEST_DATA_PATH + fileName;
        List<String[]> data = new ArrayList<>();

        logger.info("Reading CSV data from: {}", filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(","); // Default delimiter is comma
                data.add(values);
            }
            logger.info("Read {} rows from CSV file", data.size());
        } catch (IOException e) {
            logger.error("Error reading data from CSV file: {}", filePath, e);
            throw new RuntimeException("Error reading data from CSV file: " + filePath, e);
        }
        return data;
    }

    /**
     * Reads data from a CSV file with a custom delimiter.
     * @param fileName The name of the CSV file (without path)
     * @param delimiter The delimiter used in the CSV file
     * @return A list of string arrays, each representing a row in the CSV
     */
    public static List<String[]> readDataFromCsv(String fileName, String delimiter) {
        String filePath = Constants.TEST_DATA_PATH + fileName;
        List<String[]> data = new ArrayList<>();

        logger.info("Reading CSV data from: {} with delimiter: {}", filePath, delimiter);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(delimiter);
                data.add(values);
            }
            logger.info("Read {} rows from CSV file", data.size());
        } catch (IOException e) {
            logger.error("Error reading data from CSV file: {}", filePath, e);
            throw new RuntimeException("Error reading data from CSV file: " + filePath, e);
        }
        return data;
    }

    /**
     * Reads data from a JSON file.
     * @param fileName The name of the JSON file (without path)
     * @return A JSONObject representing the JSON data
     */
    public static JSONObject readDataFromJson(String fileName) {
        String filePath = Constants.TEST_DATA_PATH + fileName;

        logger.info("Reading JSON data from: {}", filePath);
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content);
        } catch (IOException e) {
            logger.error("Error reading data from JSON file: {}", filePath, e);
            throw new RuntimeException("Error reading data from JSON file: " + filePath, e);
        }
    }

    /**
     * Reads data from a JSON file as an array.
     * @param fileName The name of the JSON file (without path)
     * @return A JSONArray representing the JSON data
     */
    public static JSONArray readDataFromJsonArray(String fileName) {
        String filePath = Constants.TEST_DATA_PATH + fileName;

        logger.info("Reading JSON array data from: {}", filePath);
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONArray(content);
        } catch (IOException e) {
            logger.error("Error reading data from JSON array file: {}", filePath, e);
            throw new RuntimeException("Error reading data from JSON array file: " + filePath, e);
        }
    }

    /**
     * Converts a JSONObject to a Map.
     * @param jsonObject The JSONObject to convert
     * @return A Map representation of the JSONObject
     */
    public static Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            // Handle nested objects
            if (value instanceof JSONObject) {
                value = jsonObjectToMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = jsonArrayToList((JSONArray) value);
            }

            map.put(key, value);
        }

        return map;
    }

    /**
     * Converts a JSONArray to a List.
     * @param jsonArray The JSONArray to convert
     * @return A List representation of the JSONArray
     */
    public static List<Object> jsonArrayToList(JSONArray jsonArray) {
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);

            // Handle nested objects
            if (value instanceof JSONObject) {
                value = jsonObjectToMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = jsonArrayToList((JSONArray) value);
            }

            list.add(value);
        }

        return list;
    }

    /**
     * Gets a list of test data files in the test data directory.
     * @return A list of file names
     */
    public static List<String> getTestDataFiles() {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(Constants.TEST_DATA_PATH);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName());
                    }
                }
            }
        }

        return fileNames;
    }
}