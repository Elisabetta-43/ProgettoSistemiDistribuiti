package it.unimib.sd2025.models;

public class MessageDB {

    private final String StatusCode;
    private final String Message;
    private final String QueryResult;

    public MessageDB(String StatusCode, String Message, String QueryResult){
        this.StatusCode = StatusCode;
        this.Message = Message;
        this.QueryResult = QueryResult;
    }

    public String getStatusCode(){
        return this.StatusCode;
    }

    public String getMessage(){
        return this.Message;
    }

    public String getQueryResult(){
        return this.QueryResult;
    }

}