package it.unimib.sd2025;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String operation = query.get("op"); // Operation type (CREATE, RETRIEVE, UPDATE, DELETE)
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
            case "CREATE" -> handleCreate(id, parameters);
            case "RETRIEVE" -> handleRetrieve(id, conditions);
            case "UPDATE" -> handleUpdate(id, parameters, conditions);
            case "DELETE" -> handleDelete(id, conditions);
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

    /**
     * Sends an error response to the client.
     *
     * @param code The error code.
     * @param message The error message.
     */
    private void sendErrorResponse(String code, String message) {
        out.println(code + "_" + message + "_°");
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
     * Creates a new record in the database with the specified ID and parameters.
     *
     * @param id The ID of the record to create.
     * @param parameters The parameters for the new record.
     */
    private void handleCreate(String id, Map<String, Object> parameters) {
        if (id != null && parameters != null) {
            boolean created = database.create(id, parameters);
            if (created) {
                out.println("201_Record created successfully_°");
            } else {
                out.println("400_Error creating the record_°");
            }
        } else {
            out.println("400_Insufficient parameters for creation_°");
        }
    }

    /**
     * Handles the RETRIEVE operation.
     * Retrieves records from the database based on the specified ID or conditions.
     *
     * @param id The ID of the record to retrieve (optional).
     * @param conditions Conditions to filter which records to retrieve (optional).
     */
    private void handleRetrieve(String id, String conditions) {
        if (id != null) {
            // Retrieve the object with ID equal to id
            String result = database.retrieve(id);
            if (result != null) {
                out.println("200_Record retrieved successfully_" + result);
            } else {
                out.println("404_Record not found_°");
            }
        } else if (conditions != null) {
            // Collect all elements that meet the condition into a list
            List<Object> results = new ArrayList<>();
            for (Map.Entry<String, Object> entry : database.getAllData().entrySet()) {
                if (database.verifyCondition(conditions)) {
                    results.add(entry.getValue());
                }
            }
            if (!results.isEmpty()) {
                out.println("200_Records retrieved successfully_" + jsonb.toJson(results));
            } else {
                out.println("404_No records found matching the condition_°");
            }
        } else {
            // Collect all elements whose type matches the one specified in the query into a list
            List<Object> results = new ArrayList<>();
            for (Map.Entry<String, Object> entry : database.getAllData().entrySet()) {
                results.add(entry.getValue());
            }
            if (!results.isEmpty()) {
                out.println("200_Records retrieved successfully_" + jsonb.toJson(results));
            } else {
                out.println("404_No records found matching the condition_°");
            }
        }
    }

    /**
     * Handles the UPDATE operation.
     * Updates records in the database based on the specified ID or conditions.
     *
     * @param id The ID of the record to update (optional).
     * @param parameters The parameters to update the record with.
     * @param conditions Conditions to filter which records to update (optional).
     */
    private void handleUpdate(String id, Map<String, Object> parameters, String conditions) {
        if (id != null) {
            // Update the object with ID equal to id
            boolean updated = database.update(id, parameters);
            if (updated) {
                out.println("200_Record updated successfully_°");
            } else {
                out.println("404_Record not found_°");
            }
        } else if (conditions != null) {
            // Iterate through the database and update elements that meet the condition
            boolean updated = false;
            for (Map.Entry<String, Object> entry : database.getAllData().entrySet()) {
                if (database.verifyCondition(conditions)) {
                    database.update(entry.getKey(), parameters);
                    updated = true;
                }
            }
            if (updated) {
                out.println("200_Record updated successfully_°");
            } else {
                out.println("404_No records found matching the condition_°");
            }
        } else {
            out.println("400_Condition not specified for update_°");
        }
    }

    /**
     * Handles the DELETE operation.
     * Deletes records from the database based on the specified ID or conditions.
     *
     * @param id The ID of the record to delete (optional).
     * @param conditions Conditions to filter which records to delete (optional).
     */
    private void handleDelete(String id, String conditions) {
        if (id != null) {
            // Delete the object with ID equal to id
            boolean deleted = database.delete(id);
            if (deleted) {
                out.println("200_Record deleted successfully_°");
            } else {
                out.println("404_Record not found_°");
            }
        } else if (conditions != null) {
            // Iterate through the database and delete elements that meet the condition
            boolean deleted = false;
            for (Map.Entry<String, Object> entry : database.getAllData().entrySet()) {
                if (database.verifyCondition(conditions)) {
                    database.delete(entry.getKey());
                    deleted = true;
                }
            }
            if (deleted) {
                out.println("200_Record deleted successfully_°");
            } else {
                out.println("404_No records found matching the condition_°");
            }
        } else {
            out.println("400_Condition not specified for deletion_°");
        }
    }

}