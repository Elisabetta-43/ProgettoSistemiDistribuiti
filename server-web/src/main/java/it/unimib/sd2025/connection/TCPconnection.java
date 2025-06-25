package it.unimib.sd2025.connection;

import it.unimib.sd2025.models.MessageDB;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.*;
import java.net.Socket;

public class TCPconnection {
    private static final String HOST = "localhost";
    private static final int PORT = 3030;

    public static MessageDB sendMessage(String Query) {
        try {
            Socket socket = new Socket(HOST, PORT);
            Jsonb jsonb = JsonbBuilder.create();
            String result;

            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(Query);

            result = in.readLine();
            return jsonb.fromJson(result, MessageDB.class);
        } catch (IOException e) {
            return new MessageDB("401", "I/O exception", null);
        }
    }

}
