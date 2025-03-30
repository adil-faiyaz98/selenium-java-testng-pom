package com.example.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataUtils {

    public static List<String[]> readDataFromCsv(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(","); // You can change the delimiter if needed
                data.add(values);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading data from CSV file: " + filePath, e);
        }
        return data;
    }
}