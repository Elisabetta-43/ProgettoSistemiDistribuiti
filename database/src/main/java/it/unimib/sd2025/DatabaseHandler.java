package it.unimib.sd2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class DatabaseHandler {
    // Map for storing data in the database
    private final ConcurrentMap<String, Object> dati;
    private static DatabaseHandler instance;

    private DatabaseHandler() {
        dati = new ConcurrentHashMap<>();
    }

    /*
    * Singleton pattern to ensure only one instance of Database exists
    * This method provides a global point of access to the Database instance
    */
    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
            instance.loadInitialData();
        }
        return instance;
    }

    /**
    * Method to load initial data into the database.
    * This is done only once when the Database instance is created.
    * It reads data from JSON files and populates the 'dati' map.
    */
    private void loadInitialData() {
        Jsonb jsonb = JsonbBuilder.create();

        try {
            // Load users
            String usersJson = new String(Files.readAllBytes(Paths.get("../user.json")));
            Map<String, Object> usersData = jsonb.fromJson(usersJson, Map.class); // Deserialize as a Map
            List<Map<String, Object>> users = (List<Map<String, Object>>) usersData.get("user"); // Extract the "user" array
            for (Map<String, Object> user : users) {
                String cf = (String) user.get("fiscalCode"); 
                dati.put("User:" + cf, jsonb.toJson(user));
            }

            // Load vouchers
            String vouchersJson = new String(Files.readAllBytes(Paths.get("../voucher.json")));
            Map<String, Object> vouchersData = jsonb.fromJson(vouchersJson, Map.class); // Deserialize as a Map
            List<Map<String, Object>> vouchers = (List<Map<String, Object>>) vouchersData.get("voucher"); // Extract the "voucher" array
            for (Map<String, Object> voucher : vouchers) {
                String identifier = String.valueOf(voucher.get("idVoucher")); 
                dati.put("Voucher:" + identifier, jsonb.toJson(voucher));
            }

        } catch (IOException e) {
            System.err.println("Error loading initial data: " + e.getMessage());
        }
    }

    public Map<String, Object> getAllData() {
        return new ConcurrentHashMap<>(dati);
    }

    // Method to create an object in the database
    public boolean create(String type, String key, Map<String, Object> parameters) {
       
    }

    // Method to retrieve an object from the database
    public String retrieve(String key) {
        return (String) dati.get(key);
    }

    // Method to update an object in the database
    public boolean update(String key, String value) {
        if (key == null || value == null || !dati.containsKey(key)) {
            return false; // Missing key or value, or key does not exist
        }

        dati.put(key, value);
        return true;
    }

    // Method to delete an object from the database
    public boolean delete(String key) {
        if (key == null || !dati.containsKey(key)) {
            return false; // Missing or non-existent key
        }

        dati.remove(key);
        return true;
    }

    public boolean verifyCondition(String condition) {
       if (condition == null || condition.isEmpty()) {
           // No condition to verify
           return false;
       }
       try {
           String [] parts = condition.split("=");
           if (parts.length != 2) {
               // Invalid condition format
               return false;
           }
            String key = parts[0].trim();
            String value = parts[1].trim();
            // Check if the key exists in the database
            if (!dati.containsKey(key)) {
                return false; // Condition not met
           }                
            return dati.get(key).equals(value); // Match the value
       } catch (Exception e) {
            System.err.println("Error verifying condition: " + e.getMessage());
            return false; // Default to false in case of errors
       }}
}
