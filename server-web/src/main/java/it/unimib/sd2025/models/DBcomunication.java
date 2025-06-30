package it.unimib.sd2025.models;

import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2025.connection.TCPconnection;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class DBcomunication {

    // funzione usata per la creazione delle query
    public String MakeQuery(String op, String type, String ID, String[] keys, Object[] values, String conditions){
        Map Query = new HashMap <String, String>(), parameter;
        Jsonb jsonb = JsonbBuilder.create();
        Query.put("op", op);
        Query.put("type", type);
        Query.put("ID", ID);
        if (keys != null && values != null) {
            parameter = new HashMap <String, Object>();
                for (int i = 0; i < values.length; i++) {
                    parameter.put(keys[i], values[i]);
                }
            Query.put("parameter", jsonb.toJson(parameter));
        }else
            Query.put("parameter", null);
        Query.put("conditions", conditions);   
        return jsonb.toJson(Query);
    }

    public MessageDB SendQuery(String query){
        return TCPconnection.sendMessage(query);
    }
    


}
