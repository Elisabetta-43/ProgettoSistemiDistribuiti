package main.java.it.unimib.sd2025.models;

public class Voucher { 
    
    String Identificarore;
    Double importo;
    String tipologia;
    Boolean stato;
    String DataCreazione;
    String DataConsumo;
    String CF_utente;

    // public Voucher (int NextVoucherID, Double importo, String tipologia, String DataCreazione, String CF_utente){

    //     this.Identificarore = CF_utente + "/" + NextVoucherID;
    // }

    public String getIdentificarore() {
		return Identificarore;
	}

	public void setIdentificarore(String identificarore) {
		Identificarore = identificarore;
	}

	public Double getImporto() {
		return importo;
	}

	public void setImporto(Double importo) {
		this.importo = importo;
	}

	public String getTipologia() {
		return tipologia;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	public Boolean getStato() {
		return stato;
	}

	public void setStato(Boolean stato) {
		this.stato = stato;
	}

	public String getDataCreazione() {
		return DataCreazione;
	}

	public void setDataCreazione(String dataCreazione) {
		DataCreazione = dataCreazione;
	}

	public String getDataConsumo() {
		return DataConsumo;
	}

	public void setDataConsumo(String dataConsumo) {
		DataConsumo = dataConsumo;
	}

	public String getCF_utente() {
		return CF_utente;
	}

	public void setCF_utente(String cF_utente) {
		CF_utente = cF_utente;
	}

}

