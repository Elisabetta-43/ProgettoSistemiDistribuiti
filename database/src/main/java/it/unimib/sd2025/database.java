package it.unimib.sd2025;

import it.unimib.sd2025.models.User;
import it.unimib.sd2025.models.Voucher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Database {
    // Map for storing data in the database
    private final ConcurrentMap<String, Object> dati; 
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
        }
        return instance;
    }

    // Method to create an object in the database
    public boolean create(String key, Object value) {
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
    public boolean update(String key, Object value) {
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

    // Method to retrieve all data
    public Map<String, Object> getAllData() {
        return new HashMap<>(dati);
    }
}
