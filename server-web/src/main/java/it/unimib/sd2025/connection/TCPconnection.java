package it.unimib.sd2025.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import it.unimib.sd2025.models.MessageDB;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

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
            socket.close();
            return jsonb.fromJson(result, MessageDB.class);
        } catch (IOException e) {
            return new MessageDB("500", "I/O exception", null);
        }
    }

}
