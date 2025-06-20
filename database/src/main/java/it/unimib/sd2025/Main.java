package it.unimib.sd2025;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe principale in cui parte il database.
 */
public class Main {
    /**
     * Porta di ascolto.
     */
    public static final int PORT = 3030;

    /**
     * Istanza del database.
     */
    private static Database database;

    /**
     * Avvia il database e l'ascolto di nuove connessioni.
     */
    public static void startServer() throws IOException {
        var server = new ServerSocket(PORT);

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
        private Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                // Creazione dell'istanza del ProtocolHandler
                ProtocolHandler protocolHandler = new ProtocolHandler(Database.getInstance(), client);

                // Esecuzione del ProtocolHandler per gestire la richiesta
                protocolHandler.run();

            } catch (IOException e) {
                System.err.println("Errore nella gestione della connessione: " + e.getMessage());
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
        System.out.println("Database initialized.");
        System.out.println("Starting server...");
        // Avvio del server
        startServer();
    }
}

