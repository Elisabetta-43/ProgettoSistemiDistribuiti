package it.unimib.sd2025.models;

public class User { 

private Double contributo;
private String CF; 
private String nome;
private String cognome;
private String email;
private int NextVoucherID;

public User (String nome, String cognome, String email, String CF) {
    this.contributo = 500;
    this.CF = CF;
    this.nome = nome;
    this.cognome = cognome;
    this.email = email;
    this.NextVoucherID = 1;
}

public String getCF (){
    return this.CF;
}

public String getNome (){
    return this.nome;
}

public String getCognome (){
    return this.cognome;
}

public String getEmail (){
    return this.email;
}

public Double getContributo(){
    return this.contributo;
}

public int getNextVoucherID(){
    return this.NextVoucherID;
}

public void setContributo(Double contributo) {
	this.contributo = contributo;
}

public void setCF(String cF) {
	this.CF = cF;
}

public void setNome(String nome) {
	this.nome = nome;
}

public void setCognome(String cognome) {
	this.cognome = cognome;
}

public void setEmail(String email) {
	this.email = email;
}

public void setNextVoucherID(int nextVoucherID) {
	this.NextVoucherID = nextVoucherID;
}
}