package it.unimib.sd2025;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProtocolHandler implements Runnable {
    private final Socket clientSocket;
    private final Database database;

    public ProtocolHandler(Socket clientSocket, Database database) {
        this.clientSocket = clientSocket;
        this.database = database;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                String[] parts = request.split(" ", 3);
                String command = parts[0];

                switch (command.toUpperCase()) {
                    case "GET":
                        out.println(database.getData(parts[1]));
                        break;
                    case "GETALL":
                        out.println(database.getAllData());
                        break;
                    case "SAVE":
                        database.addData(parts[1], parts[2]);
                        out.println("OK");
                        break;
                    case "DELETE":
                        database.removeData(parts[1]);
                        out.println("OK");
                        break;
                    case "EXISTS":
                        out.println(database.containsKey(parts[1]) ? "YES" : "NO");
                        break;
                    default:
                        out.println("ERROR: Unknown command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}