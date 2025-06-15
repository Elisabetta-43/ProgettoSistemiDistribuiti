package it.unimib.sd2025;
//necessario aggiungere un package per evitare conflitti di nomi con altre classi??
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DatabaseServer {
    private static final int PORT = 3030;
    private final Database database;

    public DatabaseServer() {
        this.database = Database.getInstance();
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Database server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ProtocolHandler(clientSocket, database)).start();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new DatabaseServer().start();
    }
}