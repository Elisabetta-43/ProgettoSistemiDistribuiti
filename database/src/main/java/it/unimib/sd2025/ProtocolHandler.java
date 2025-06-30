package it.unimib.sd2025;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimib.sd2025.models.MessageDB;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;

public class ProtocolHandler implements Runnable {
    private final DatabaseHandler database; // Database instance
    private final Socket clientSocket; // Client socket for communication
    private final PrintWriter out; // Output stream to send responses to the client
    private final BufferedReader in; // Input stream to read requests from the client
    private final Jsonb jsonb; // JSON-B instance for serialization/deserialization

    /**
     * Constructor for ProtocolHandler.
     *
     * @param database The database instance to interact with.
     * @param clientSocket The socket connected to the client.
     * @throws IOException If an I/O error occurs when creating input/output streams.
     */
    public ProtocolHandler(DatabaseHandler database, Socket clientSocket) throws IOException {
        this.database = database;
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.jsonb = JsonbBuilder.create(); // Initialize JSON-B
    }

    @Override
    /**
     * The run method that handles the client requests.
     * It reads requests from the client, processes them, and sends responses.
     */
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                try {
                    // Deserialize the JSON request into a Map.
                    Map<String, String> query = jsonb.fromJson(request, Map.class);

                    // Validate mandatory parameters
                    if (!validateQuery(query)) {
                        sendErrorResponse("500", "Missing mandatory parameters in the request");
                        continue; // Skip processing the request
                    }

                    // Process the request
                    processRequest(query);
                } catch (JsonbException e) {
                    logError("Error during JSON-B deserialization", e);
                    sendErrorResponse("500", "Error processing the request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logError("Error handling the connection", e);
        } finally {
            closeResources();
        }
    }

    private void processRequest(Map<String, String> query) {
        String operation = query.get("op");
        String type = query.get("type");
        String id = query.get("ID");
        String parameterJson = query.get("parameter");
        String conditions = query.get("conditions");
        Map<String, Object> parameters = null;

        // Validate specific parameters
        if (parameterJson != null) {
            try {
                parameters = jsonb.fromJson(parameterJson, Map.class);
            } catch (JsonbException e) {
                sendErrorResponse("500", "Error deserializing parameters");
                return;
            }
        }

        switch (operation.toUpperCase()) {
            case "CREATE" -> handleCreate(type, id, parameters);
            case "RETRIEVE" -> handleRetrieve(type, id, conditions);
            case "UPDATE" -> handleUpdate(type, id, parameters, conditions);
            case "DELETE" -> handleDelete(type, id, conditions);
            default -> sendErrorResponse("500", "Unsupported operation");
        }
    }

    /**
     * Validates the query parameters to ensure mandatory fields are present.
     *
     * @param query The query parameters from the client request.
     * @return true if the query is valid, false otherwise.
     */
    private boolean validateQuery(Map<String, String> query) {
        return query.containsKey("op") && query.containsKey("type");
    }

    private void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
    }

    private void sendErrorResponse(String code, String message) {
        out.println(jsonb.toJson(new MessageDB(code, message, null)));
    }

    private void closeResources() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            logError("Error closing resources", e);
        }
    }

    /**
     * Handles the CREATE operation.
     * Creates a new record in the database with the specified type and ID.
     *
     * @param type The type of the record to create.
     * @param id The ID of the record to create.
     * @param parameters The parameters for the new record.
     */
    private void handleCreate(String type, String id, Map<String, Object> parameters) {
        if (id != null && parameters != null) {
            boolean created = database.create(type, id, parameters);
            if (created) {
                out.println(jsonb.toJson(new MessageDB("201", "Record created successfully", null)));
            } else {
                out.println(jsonb.toJson(new MessageDB("400", "Error creating the record", null)));
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Insufficient parameters for creation", null)));
        }
    }

    /**
     * Handles the RETRIEVE operation.
     * Retrieves records from the database based on the specified type, ID, or condition.
     *
     * @param type The type of records to retrieve.
     * @param id The ID of the record to retrieve (optional).
     * @param conditions Conditions to filter the records (optional).
     */
    private void handleRetrieve(String type, String id, String conditions) {
        if (id != null) {
            // Retrieve the object with ID equal to id
            String result = database.retrieve(type + ":" + id);
            if (result != null) {
                out.println(jsonb.toJson(new MessageDB("200", "Record retrieved successfully", result)));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "Record not found", null)));
            }
        } else if (conditions != null) {
            // Collect all elements that meet the condition into a list
            List<Object> results = new ArrayList<>();
            for (Map.Entry<String, Object> entry: database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && database.verifyCondition( conditions)) {
                    results.add(entry.getValue());
                }
            }
            if (!results.isEmpty()) {
                out.println(jsonb.toJson(new MessageDB("200", "Records retrieved successfully", jsonb.toJson(results))));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "No records found matching the condition", null)));
            }
        } else {
            // Collect all elements whose type matches the one specified in the query into a list
             List<Object> results = new ArrayList<>();
            for (Map.Entry<String, Object> entry: database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":")) {
                    results.add(entry.getValue());
                }
            }
            if (!results.isEmpty()) {
                out.println(jsonb.toJson(new MessageDB("200", "Records retrieved successfully", jsonb.toJson(results))));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "No records found matching the condition", null)));
            }
        }
    }

    /**
     * Handles the UPDATE operation.
     * Updates records in the database based on the specified type, ID, or condition.
     *
     * @param type The type of records to update.
     * @param id The ID of the record to update (optional).
     * @param parameters The parameters to update in the record.
     * @param conditions Conditions to filter which records to update (optional).
     */
    private void handleUpdate(String type, String id, Map<String, Object> parameters, String conditions) {
        if (id != null) {
            // Update the object with ID equal to id
            boolean updated = database.update(type + ":" + id, jsonb.toJson(parameters));
            if (updated) {
                out.println(jsonb.toJson(new MessageDB("200", "Record updated successfully", null)));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "Record not found", null)));
            }
        } else if (conditions != null) {
            // Iterate through the database and update elements that meet the condition
            for (Map.Entry<String, Object> entry: database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && database.verifyCondition(conditions)) {
                    database.update(entry.getKey(), jsonb.toJson(parameters));
                    out.println(jsonb.toJson(new MessageDB("200", "Record updated successfully", null)));
                }
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Condition not specified for update", null)));
        }
    }

    /**
     * Handles the DELETE operation.
     * Deletes records from the database based on the specified type, ID, or condition.
     *
     * @param type The type of records to delete.
     * @param id The ID of the record to delete (optional).
     * @param conditions Conditions to filter which records to delete (optional).
     */
    private void handleDelete(String type, String id, String conditions) {
        if (id != null) {
            // Delete the object with ID equal to id
            boolean deleted = database.delete(type + ":" + id);
            if (deleted) {
                out.println(jsonb.toJson(new MessageDB("200", "Record deleted successfully", null)));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "Record not found", null)));
            }
        } else if (conditions != null) {
            // Iterate through the database and delete elements that meet the condition
            for (Map.Entry<String, Object> entry: database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && database.verifyCondition(conditions)) {
                    database.delete(entry.getKey());
                    out.println(jsonb.toJson(new MessageDB("200", "Record deleted successfully", null)));
                }
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Condition not specified for deletion", null)));
        }
    }

}