package it.unimib.sd2025;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import it.unimib.sd2025.models.MessageDB;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;

public class ProtocolHandler implements Runnable {
    private final Database database; // Istanza del database
    private final Socket clientSocket; // Socket del client
    private final PrintWriter out; // Stream di output per inviare risposte al client
    private final BufferedReader in; // Stream di input per leggere le richieste dal client
    private final Jsonb jsonb; // Istanza di JSON-B per la serializzazione/deserializzazione

    public ProtocolHandler(Database database, Socket clientSocket) throws IOException {
        this.database = database;
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.jsonb = JsonbBuilder.create(); // Inizializza JSON-B
    }

    @Override
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                try {
                    // Deserializzazione della richiesta JSON-B in una mappa
                    Map<String, Object> query = jsonb.fromJson(request, Map.class);

                    // Estrazione dei parametri principali dalla query
                    String operation = (String) query.get("op");
                    String type = (String) query.get("type");
                    String id = (String) query.get("ID");
                    Map<String, Object> parameters = (Map<String, Object>) query.get("parameter");
                    String conditions = (String) query.get("conditions");

                    // Gestione delle operazioni
                    switch (operation.toUpperCase()) {
                        case "CREATE":
                            handleCreate(type, id, parameters);
                            break;
                        case "RETRIEVE":
                            handleRetrieve(type, id, parameters, conditions);
                            break;
                        case "UPDATE":
                            handleUpdate(type, id, parameters, conditions);
                            break;
                        case "DELETE":
                            handleDelete(type, id, conditions);
                            break;
                        default:
                            out.println(jsonb.toJson(new MessageDB("400", "Operazione non supportata", null)));
                    }
                } catch (JsonbException e) {
                    out.println(jsonb.toJson(new MessageDB("500", "Errore durante l'elaborazione della richiesta: " + e.getMessage(), null)));
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella gestione della connessione: " + e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Errore nella chiusura delle risorse: " + e.getMessage());
            }
        }
    }

    private void handleCreate(String type, String id, Map<String, Object> parameters) {
        if (id != null && parameters != null) {
            // Create a new record in the database
            jakarta.json.JsonObject jsonParameters = jsonb.fromJson(jsonb.toJson(parameters), jakarta.json.JsonObject.class);
            boolean created = database.create(type, id, jsonParameters);
            if (created) {
                out.println(jsonb.toJson(new MessageDB("201", "Record creato con successo", null)));
            } else {
                out.println(jsonb.toJson(new MessageDB("400", "Errore durante la creazione del record", null)));
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Parametri insufficienti per la creazione", null)));
        }
    }

    private void handleRetrieve(String type, String id, Map<String, Object> parameters, String conditions) {
        if (id != null) {
            // Prelevo dal database l'oggetto con ID uguale a id
            Object result = database.retrieve(type, id, null, null); // Adjusted to pass required arguments
            if (result != null) {
                out.println(jsonb.toJson(new MessageDB("200", "Record recuperato con successo", result.toString())));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "Record non trovato", null)));
            }
        } else if (conditions != null) {
            // Ciclo il database e verifico quali elementi rispettano la condizione
            for (Map.Entry<String, String> entry : database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && entry.getValue().contains(conditions)) {
                    out.println(jsonb.toJson(new MessageDB("200", "Record recuperato con successo", entry.getValue())));
                }
            }
        } else {
            // Prelevo tutti gli elementi il cui tipo è uguale a quello specificato nella query
            for (Map.Entry<String, String> entry : database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":")) {
                    out.println(jsonb.toJson(new MessageDB("200", "Record recuperato con successo", entry.getValue())));
                }
            }
        }
    }

    private void handleUpdate(String type, String id, Map<String, Object> parameters, String conditions) {
        if (id != null) {
            // Aggiorno l'oggetto con ID uguale a id
            jakarta.json.JsonObject jsonParameters = jsonb.fromJson(jsonb.toJson(parameters), jakarta.json.JsonObject.class);
            boolean updated = database.update(type + ":" + id, jsonParameters, null);
            if (updated) {
                out.println(jsonb.toJson(new MessageDB("200", "Record aggiornato con successo", null)));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "Record non trovato", null)));
            }
        } else if (conditions != null) {
            // Ciclo il database e aggiorno gli elementi che rispettano la condizione
            for (Map.Entry<String, String> entry : database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && entry.getValue().contains(conditions)) {
                    jakarta.json.JsonObject jsonParameters = jsonb.fromJson(jsonb.toJson(parameters), jakarta.json.JsonObject.class);
                    database.update(entry.getKey(), jsonParameters, null);
                    out.println(jsonb.toJson(new MessageDB("200", "Record aggiornato con successo", null)));
                }
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Condizioni non specificate per l'aggiornamento", null)));
        }
    }

    private void handleDelete(String type, String id, String conditions) {
        if (id != null) {
            // Elimino l'oggetto con ID uguale a id
            boolean deleted = database.delete(type + ":" + id, jakarta.json.Json.createArrayBuilder().build());
            if (deleted) {
                out.println(jsonb.toJson(new MessageDB("200", "Record eliminato con successo", null)));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "Record non trovato", null)));
            }
        } else if (conditions != null) {
            // Ciclo il database e elimino gli elementi che rispettano la condizione
            for (Map.Entry<String, String> entry : database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && entry.getValue().contains(conditions)) {
                    database.delete(entry.getKey(), jakarta.json.Json.createArrayBuilder().build());
                    out.println(jsonb.toJson(new MessageDB("200", "Record eliminato con successo", null)));
                }
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Condizioni non specificate per l'eliminazione", null)));
        }
    }
}