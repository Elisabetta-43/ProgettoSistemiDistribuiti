package it.unimib.sd2025.models;

public class User { 

    private Double contributions;
    private String fiscalCode; 
    private String name; 
    private String surname;
    private String Email;
    private int NextVoucherID;
    private boolean Admin;
    private String type;

    public String getfiscalCode (){
        return this.fiscalCode;
    }

    public String getname (){
        return this.name;
    }

    public String getsurname (){
        return this.surname;
    }

    public String getEmail (){
        return this.Email;
    }

    public Double getcontributions(){
        return this.contributions;
    }

    public int getNextVoucherID(){
        return this.NextVoucherID;
    }

    public boolean getAdmin(){
        return this.Admin;
    }

    public void setcontributions(Double contributions) {
        this.contributions = contributions;
    }

    public void setfiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public void setname(String name) {
        this.name = name;
    }

    public void setsurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setNextVoucherID(int nextVoucherID) {
        this.NextVoucherID = nextVoucherID;
    }

    public void setAdmin(boolean admin){
        this.Admin = admin;
    }

    public String gettype(){
        return this.type;
    }

    public void settype(String type){
        this.type = type;
    }


    }