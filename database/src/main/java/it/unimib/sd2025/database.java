package it.unimib.sd2025;

//import it.unimib.sd2025.models.User;
//import it.unimib.sd2025.models.Voucher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Database {
    private final ConcurrentMap<String, Object> dati; // Mappa per memorizzare i dati
    private static Database instance;

    private Database() {
        dati = new ConcurrentHashMap<>();
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Metodo per aggiungere un oggetto al database
    public boolean create(String key, Object value) {
        if (key == null || value == null) {
            return false; // Chiave o valore mancanti
        }

        if (dati.containsKey(key)) {
            return false; // L'oggetto esiste già
        }

        dati.put(key, value);
        return true;
    }

    // Metodo per recuperare un oggetto dal database
    public Object retrieve(String key) {
        return dati.get(key);
    }

    // Metodo per aggiornare un oggetto nel database
    public boolean update(String key, Object value) {
        if (key == null || value == null || !dati.containsKey(key)) {
            return false; // Chiave o valore mancanti, oppure chiave non esistente
        }

        dati.put(key, value);
        return true;
    }

    // Metodo per eliminare un oggetto dal database
    public boolean delete(String key) {
        if (key == null || !dati.containsKey(key)) {
            return false; // Chiave mancante o non esistente
        }

        dati.remove(key);
        return true;
    }

    // Metodo per ottenere tutti i dati
    public Map<String, Object> getAllData() {
        return new HashMap<>(dati);
    }
}
