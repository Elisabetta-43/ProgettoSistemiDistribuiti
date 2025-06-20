package it.unimib.sd2025;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonArray;

import java.io.*;
import java.net.Socket;

public class ProtocolHandler implements Runnable {
    private final Database database; // Istanza del database
    private final Socket clientSocket; // Socket del client
    private PrintWriter out; // Stream di output per inviare risposte al client
    private BufferedReader in; // Stream di input per leggere le richieste dal client

    public ProtocolHandler(Database database, Socket clientSocket) throws IOException {
        this.database = database;
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                try {
                    // Parsing della richiesta JSON
                    JsonReader jsonReader = Json.createReader(new StringReader(request));
                    JsonObject jsonRequest = jsonReader.readObject();

                    // Estrazione dei campi principali
                    String operation = jsonRequest.getString("op").toUpperCase();
                    String type = jsonRequest.getString("type");
                    String id = jsonRequest.containsKey("ID") ? jsonRequest.getString("ID") : null;
                    JsonObject parameters = jsonRequest.containsKey("parameter") ? jsonRequest.getJsonObject("parameter") : null;
                    JsonArray conditions = jsonRequest.containsKey("conditions") ? jsonRequest.getJsonArray("conditions") : null;

                    // Gestione delle operazioni
                    switch (operation) {
                        case "CREATE":
                            handleCreate(type, id, parameters);
                            break;
                        case "RETRIEVE":
                            handleRetrieve(type, id, parameters, conditions);
                            break;
                        case "UPDATE":
                            handleUpdate(type, parameters, conditions);
                            break;
                        case "DELETE":
                            handleDelete(type, conditions);
                            break;
                        default:
                            out.println("{\"status\": 400, \"message\": \"Operazione non supportata\"}");
                    }
                } catch (Exception e) {
                    out.println("{\"status\": 500, \"message\": \"Errore durante l'elaborazione della richiesta: " + e.getMessage() + "\"}");
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

    private void handleCreate(String type, String id, JsonObject parameters) {
        if (database.create(type, id, parameters)) {
            out.println("{\"status\": 201, \"message\": \"Record creato con successo\"}");
        } else {
            out.println("{\"status\": 500, \"message\": \"Errore nella creazione del record\"}");
        }
    }

    private void handleRetrieve(String type, String id, JsonObject parameters, JsonArray conditions) {
        // Implementazione della logica per il recupero di un record
        var results = database.retrieve(type, id, parameters, conditions);
        if (results != null && !results.isEmpty()) {
            out.println("{\"status\": 200, \"message\": \"Record recuperati con successo\", \"data\": " + results + "}");
        } else {
            out.println("{\"status\": 404, \"message\": \"Record non trovato\"}");
        }
    }

    private void handleUpdate(String type, JsonObject parameters, JsonArray conditions) {
        if (database.update(type, parameters, conditions)) {
            out.println("{\"status\": 200, \"message\": \"Record aggiornato con successo\"}");
        } else {
            out.println("{\"status\": 500, \"message\": \"Errore nell'aggiornamento del record\"}");
        }
    }

    private void handleDelete(String type, JsonArray conditions) {
        if (database.delete(type, conditions)) {
            out.println("{\"status\": 200, \"message\": \"Record cancellato con successo\"}");
        } else {
            out.println("{\"status\": 500, \"message\": \"Errore nella cancellazione del record\"}");
        }
    }
}