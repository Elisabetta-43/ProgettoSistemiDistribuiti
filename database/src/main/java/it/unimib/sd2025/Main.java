package it.unimib.sd2025;

//import it.unimib.sd2025.models.User;
//import it.unimib.sd2025.models.Voucher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main class for the database server. 
 * It initializes the database and listens for incoming client connections.
 * Each connection is handled by a separate thread using the ProtocolHandler.
 */
public class Main {
    // Listener port for the database server */
    public static final int PORT = 3030;

    // Database instance 
    private static Database database;
    
    /**
     * Start the database server and listen for client connections.
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
     * Handler di una connessione del client.
     */
    private static class Handler extends Thread {
        private final Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        @Override
        /**
         * Execute the ProtocolHandler to manage the client's request.
         * This method runs in a separate thread for each client connection.
         * If an IOException occurs, it prints an error message.
         * Finally, it ensures that the client socket is closed.
         */
        public void run() {
            try {
                // Creation of a ProtocolHandler instance
                ProtocolHandler protocolHandler = new ProtocolHandler(Database.getInstance(), client);

                // Esecuzione del ProtocolHandler per gestire la richiesta
                protocolHandler.run();

            } catch (IOException ex) {
            } finally {
                try {
                    client.close(); // Chiude il socket del client
                } catch (IOException e) {
                    System.err.println("Errore nella chiusura del socket: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Metodo principale di avvio del database.
     *
     * @param args argomenti passati a riga di comando.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        database = Database.getInstance();
        Database.getInstance(); // Initialize the database
        System.out.println("Starting server...");
        // Avvio del server
        startServer();
    }
}

