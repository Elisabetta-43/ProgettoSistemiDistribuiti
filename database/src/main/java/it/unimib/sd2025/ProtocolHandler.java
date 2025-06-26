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
    private final Database database; // Database instance
    private final Socket clientSocket; // Client socket for communication
    private final PrintWriter out; // Output stream to send responses to the client
    private final BufferedReader in; // Input stream to read requests from the client
    private final Jsonb jsonb; // JSON-B instance for serialization/deserialization

    public ProtocolHandler(Database database, Socket clientSocket) throws IOException {
        this.database = database;
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.jsonb = JsonbBuilder.create(); // Initialize JSON-B
    }

    @Override
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                try {
                    // Deserialize the JSON request
                    Map<String, String> query = jsonb.fromJson(request, Map.class);

                    // Validate mandatory parameters
                    if (!validateQuery(query)) {
                        sendErrorResponse("400", "Missing mandatory parameters in the request");
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
        Map<String, String> parameters = null;

        // Validate specific parameters
        if (parameterJson != null) {
            try {
                parameters = jsonb.fromJson(parameterJson, Map.class);
            } catch (JsonbException e) {
                sendErrorResponse("400", "Error deserializing parameters");
                return;
            }
        }

        switch (operation.toUpperCase()) {
            case "CREATE" -> handleCreate(type, id, parameters);
            case "RETRIEVE" -> handleRetrieve(type, id, parameters, query.get("conditions"));
            case "UPDATE" -> handleUpdate(type, id, parameters, query.get("conditions"));
            case "DELETE" -> handleDelete(type, id, query.get("conditions"));
            default -> sendErrorResponse("400", "Unsupported operation");
        }
    }

    private boolean validateQuery(Map<String, String> query) {
        // Verify that mandatory parameters are present
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

    private void handleCreate(String type, String id, Map<String, String> parameters) {
        if (id != null && parameters != null) {
            // Create a new record in the database
            // Assuming parameters is a map containing the necessary fields for creation
            boolean created = database.create(type + ":" + id, jsonb.toJson(parameters));
            if (created) {
                out.println(jsonb.toJson(new MessageDB("201", "Record created successfully", null)));
            } else {
                out.println(jsonb.toJson(new MessageDB("400", "Error creating the record", null)));
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Insufficient parameters for creation", null)));
        }
    }

    private void handleRetrieve(String type, String id, Map<String, String> parameters, String conditions) {
        if (id != null) {
            // Retrieve the object from the database with ID equal to id
            Object result = database.retrieve(type + ":" + id); // Adjusted to pass required arguments
            if (result != null) {
                out.println(jsonb.toJson(new MessageDB("200", "Record retrieved successfully", result.toString())));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "Record not found", null)));
            }
        } else if (conditions != null) {
            // Iterate through the database and check which elements meet the condition
            for (Map.Entry<String, String> entry : database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && ((String) entry.getValue()).contains(conditions)) {
                    out.println(jsonb.toJson(new MessageDB("200", "Record retrieved successfully", (String) entry.getValue())));
                }
            }
        } else {
            // Retrieve all elements whose type matches the one specified in the query
            for (Map.Entry<String, String> entry : database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":")) {
                    out.println(jsonb.toJson(new MessageDB("200", "Record retrieved successfully", (String) entry.getValue())));
                }
            }
        }
    }

    private void handleUpdate(String type, String id, Map<String, String> parameters, String conditions) {
        if (id != null) {
            // Update the object with ID equal to id
            // Assuming parameters is a map containing the fields to update
            boolean updated = database.update(type + ":" + id, jsonb.toJson(parameters));
            if (updated) {
                out.println(jsonb.toJson(new MessageDB("200", "Record updated successfully", null)));
            } else {
                out.println(jsonb.toJson(new MessageDB("404", "Record not found", null)));
            }
        } else if (conditions != null) {
            // Iterate through the database and update elements that meet the condition
            for (Map.Entry<String, String> entry : database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && ((String) entry.getValue()).contains(conditions)) {
                    database.update(entry.getKey(), jsonb.toJson(parameters));
                    out.println(jsonb.toJson(new MessageDB("200", "Record updated successfully", null)));
                }
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Conditions not specified for update", null)));
        }
    }

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
            for (Map.Entry<String, String> entry : database.getAllData().entrySet()) {
                if (entry.getKey().startsWith(type + ":") && ((String) entry.getValue()).contains(conditions)) {
                    database.delete(entry.getKey());
                    out.println(jsonb.toJson(new MessageDB("200", "Record deleted successfully", null)));
                }
            }
        } else {
            out.println(jsonb.toJson(new MessageDB("400", "Conditions not specified for deletion", null)));
        }
    }
}