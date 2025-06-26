package it.unimib.sd2025;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main class for the database server. 
 * It initializes the database and listens for incoming client connections.
 * Each connection is handled by a separate thread using the ProtocolHandler.
 */
public class Main {
    // Listener port for the database server
    public static final int PORT = 3030;
    
    /**
     * Starts the database server and listens for client connections.
     *
     * @throws IOException if an I/O error occurs when opening the socket.
     */     
    public static void startServer() throws IOException {
        ServerSocket server = new ServerSocket(PORT);

        System.out.println("Database listening at localhost:" + PORT);

        try {
            while (true)
                new Handler(server.accept()).start();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            server.close();
        }
    }

    /**
     * Handler for a client connection.
     * This class manages the client connection in a separate thread.
     */
    private static class Handler extends Thread {
        private final Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        @Override
        /**
         * Executes the ProtocolHandler to manage the client's request.
         * This method runs in a separate thread for each client connection.
         * If an IOException occurs, it prints an error message.
         * Finally, it ensures that the client socket is closed.
         */
        public void run() {
            try {
                // Create a ProtocolHandler instance
                ProtocolHandler protocolHandler = new ProtocolHandler(Database.getInstance(), client);

                // Execute the ProtocolHandler to handle the request
                protocolHandler.run();

            } catch (IOException ex) {
                System.err.println("Error during client handling: " + ex.getMessage());
            } finally {
                try {
                    client.close(); // Close the client socket
                } catch (IOException e) {
                    System.err.println("Error closing the socket: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Main method to start the database server.
     *
     * @param args command-line arguments.
     *
     * @throws IOException if an I/O error occurs during server startup.
     */
    public static void main(String[] args) throws IOException {
        Database.getInstance(); // Initialize the database
        System.out.println("Starting server...");
        startServer(); // Start the server
    }
}

