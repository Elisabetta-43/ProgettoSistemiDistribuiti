package it.unimib.sd2025;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jakarta.json.JsonObject;
import jakarta.json.JsonArray;
import com.google.gson.Gson;

public class Database {
    private final ConcurrentMap<String, String> dati; // Mappa per memorizzare i dati
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

    // Metodo per ottenere tutte le coppie chiave-valore del database
    public Map<String, String> getAllData() {
        return new ConcurrentHashMap<>(dati);
    }

    // Metodo per verificare se una chiave esiste nel database
    public boolean containsKey(String key) {
        return dati.containsKey(key);
    }

    // Metodo per eliminare oggetti dal database
    public boolean delete(String type, JsonArray conditions) {
        boolean deleted = false;

        for (String key : new ArrayList<>(dati.keySet())) {
            if (key.startsWith(type + ":")) {
                Map<String, Object> object = parseJson(dati.get(key));

                // Filtra per condizioni
                if (conditions != null && !matchConditions(object, conditions)) {
                    continue;
                }

                dati.remove(key);
                deleted = true;
            }
        }

        return deleted;
    }

    // Metodo per aggiornare oggetti nel database
    public boolean update(String type, JsonObject parameters, JsonArray conditions) {
        boolean updated = false;

        for (Map.Entry<String, String> entry : dati.entrySet()) {
            if (entry.getKey().startsWith(type + ":")) {
                Map<String, Object> object = parseJson(entry.getValue());

                // Filtra per condizioni
                if (conditions != null && !matchConditions(object, conditions)) {
                    continue;
                }

                // Aggiorna i parametri
                for (String key : parameters.keySet()) {
                    object.put(key, parameters.get(key));
                }
                dati.put(entry.getKey(), toJson(object));
                updated = true;
            }
        }

        return updated;
    }

    // Metodo per recuperare oggetti dal database
    public List<String> retrieve(String type, String id, JsonObject parameters, JsonArray conditions) {
        List<String> results = new ArrayList<>();

        for (Map.Entry<String, String> entry : dati.entrySet()) {
            if (entry.getKey().startsWith(type + ":")) {
                Map<String, Object> object = parseJson(entry.getValue());

                // Filtra per ID (se specificato)
                if (id != null && !entry.getKey().equals(type + ":" + id)) {
                    continue;
                }

                // Filtra per condizioni
                if (conditions != null && !matchConditions(object, conditions)) {
                    continue;
                }

                // Seleziona i parametri richiesti
                if (parameters != null) {
                    Map<String, Object> filteredObject = new HashMap<>();
                    for (String param : parameters.keySet()) {
                        if (parameters.getBoolean(param, false)) {
                            filteredObject.put(param, object.get(param));
                        }
                    }
                    results.add(toJson(filteredObject));
                } else {
                    results.add(entry.getValue()); // Recupera tutti i parametri
                }
            }
        }

        return results;
    }

    // Metodo per creare un nuovo oggetto nel database
    public boolean create(String type, String id, JsonObject parameters) {
        if (id == null || parameters == null) {
            return false; // ID o parametri mancanti
        }

        String key = type + ":" + id;
        if (dati.containsKey(key)) {
            return false; // L'oggetto esiste già
        }

        dati.put(key, parameters.toString()); // Aggiunge l'oggetto al database
        return true;
    }

    // Metodo per convertire una stringa JSON in una mappa
    private Map<String, Object> parseJson(String json) {
        return new Gson().fromJson(json, Map.class);
    }

    // Metodo per convertire una mappa in una stringa JSON
    private String toJson(Map<String, Object> map) {
        return new Gson().toJson(map);
    }

    // Metodo per verificare se un oggetto soddisfa le condizioni
    private boolean matchConditions(Map<String, Object> object, JsonArray conditions) {
        if (conditions == null) return true;

        for (var conditionValue : conditions) {
            JsonArray condition = conditionValue.asJsonArray();
            String param = condition.getString(0);
            String operator = condition.getString(1);
            Object value = condition.get(2);

            Object objectValue = object.get(param);

            switch (operator) {
                case "=":
                    if (!Objects.equals(objectValue, value)) return false;
                    break;
                case ">":
                    if (!(objectValue instanceof Number) || ((Number) objectValue).doubleValue() <= ((Number) value).doubleValue()) return false;
                    break;
                case "<":
                    if (!(objectValue instanceof Number) || ((Number) objectValue).doubleValue() >= ((Number) value).doubleValue()) return false;
                    break;
                default:
                    return false;
            }
        }

        return true;
    }
}
