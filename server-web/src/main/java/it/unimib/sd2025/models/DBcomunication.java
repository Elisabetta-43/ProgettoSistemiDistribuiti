package it.unimib.sd2025.models;

import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2025.connection.TCPconnection;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class DBcomunication {

    
    public static MessageDB CreateNewUser(String user){
        Map Query = new HashMap <String, String>();
        Jsonb jsonb = JsonbBuilder.create();
        String QueryString;

        Query.put("op", "CREATE");
        Query.put("type", "User");
        Query.put("ID", "CF");
        Query.put("parameter", user);
        //Query.put("conditions", null);
        QueryString = jsonb.toJson(Query);

        return TCPconnection.sendMessage(QueryString);
    }

    public static MessageDB CreateNewVoucher(String voucher){
        Map Query = new HashMap <String, String>();
        Jsonb jsonb = JsonbBuilder.create();
        String QueryString;

        Query.put("op", "CREATE");
        Query.put("type", "Voucher");
        Query.put("ID", "Identificatore");
        Query.put("parameter", voucher);
        //Query.put("conditions", null);
        QueryString = jsonb.toJson(Query);

        return TCPconnection.sendMessage(QueryString);
    }

    public static MessageDB GetParam(String parameter, String ID, String type){
        Map Query = new HashMap <String, String>();
        Jsonb jsonb = JsonbBuilder.create();
        String QueryString;

        Query.put("op", "RETRIEVE");
        Query.put("type", type);
        Query.put("parameter", parameter);
        if (type.equals("User"))
            Query.put("conditions", "CF,=,"+ ID);
        else
            Query.put("conditions", "Identificatore,=,"+ ID);
        QueryString = jsonb.toJson(Query);

        return TCPconnection.sendMessage(QueryString);  
    }

    public static MessageDB SetParam(String parameter, String ID, String type){
        Map Query = new HashMap <String, String>();
        Jsonb jsonb = JsonbBuilder.create();
        String QueryString;

        Query.put("op", "UPDATE");
        Query.put("type", type);
        Query.put("parameter", parameter);
        if (type.equals("User"))
            Query.put("conditions", "CF,=,"+ ID);
        else
            Query.put("conditions", "Identificatore,=,"+ ID);
        QueryString = jsonb.toJson(Query);

        return TCPconnection.sendMessage(QueryString);  
    }

    public static MessageDB DeleteVoucher(String ID){
        Map Query = new HashMap <String, String>();
        Jsonb jsonb = JsonbBuilder.create();
        String QueryString;
        
        Query.put("op", "DELETE");
        Query.put("type", "Voucher");
        Query.put("parameter", null);
        Query.put("conditions", "Identificatore,=,"+ ID);
        QueryString = jsonb.toJson(Query);

        return TCPconnection.sendMessage(QueryString);  
    }

    public static MessageDB GetVoucherList(String CF, String VoucherParam){
        Map Query = new HashMap <String, String>();
        Jsonb jsonb = JsonbBuilder.create();
        String QueryString;

        Query.put("op", "RETRIEVE");
        Query.put("type", "Voucher");
        Query.put("parameter", VoucherParam);
        Query.put("conditions", "CF_utente,=,"+ CF);
        QueryString = jsonb.toJson(Query);

        return TCPconnection.sendMessage(QueryString);  
    }
}
