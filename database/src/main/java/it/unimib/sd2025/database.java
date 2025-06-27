package it.unimib.sd2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import it.unimib.sd2025.models.User;
import it.unimib.sd2025.models.Voucher;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class Database {
    // Map for storing data in the database
    private final ConcurrentMap<String, String> dati; 
    private static Database instance;

    private Database() {
        dati = new ConcurrentHashMap<>();
    }

    /*
    * Singleton pattern to ensure only one instance of Database exists
    * This method provides a global point of access to the Database instance
    */
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
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
            String usersJson = new String(Files.readAllBytes(Paths.get("user.json")));
            List<User> users = jsonb.fromJson(usersJson, new ArrayList<User>() {}.getClass().getGenericSuperclass());
            for (User user : users) {
                dati.put("User:" + user.getCF(), jsonb.toJson(user));
            }

            // Load vouchers
            String vouchersJson = new String(Files.readAllBytes(Paths.get("voucher.json")));
            List<Voucher> vouchers = jsonb.fromJson(vouchersJson, new ArrayList<Voucher>() {}.getClass().getGenericSuperclass());
            for (Voucher voucher : vouchers) {
                dati.put("Voucher:" + voucher.getIdentificarore(), jsonb.toJson(voucher));
            }

             // Load global state
            String globalStateJson = new String(Files.readAllBytes(Paths.get("database.json")));
            Map<String, String> globalState = jsonb.fromJson(globalStateJson, Map.class);
            dati.put("GlobalState", jsonb.toJson(globalState));

        } catch (IOException e) {
            System.err.println("Error loading initial data: " + e.getMessage());
        }
    }

    public Map<String, String> getAllData() {
        return new ConcurrentHashMap<>(dati);
    }

    // Method to create an object in the database
    public boolean create(String key, String value) {
        if (key == null || value == null) {
            // Missing key or value
            return false; 
        }

        if (dati.containsKey(key)) {
            // The object already exists
            return false; 
        }

        dati.put(key, value);
        return true;
    }

    // Method to retrieve an object from the database
    public Object retrieve(String key) {
        return dati.get(key);
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
}
