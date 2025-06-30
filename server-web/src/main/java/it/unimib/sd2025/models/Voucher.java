package it.unimib.sd2025.models;

public class Voucher { 
    
    String idVoucher;
    Double amount;
    String category;
    Boolean status; // true se attivo, false altrimenti
    String creationDate;
    String expirationDate;
    String userId;

    // public Voucher (int NextVoucherID, Double amount, String category, String creationDate, String userId){

    //     this.idVoucher = userId + "/" + NextVoucherID;
    // }

    public String getidVoucher() {
		return idVoucher;
	}

	public void setidVoucher(String idVoucher) {
		this.idVoucher = idVoucher;
	}

	public Double getamount() {
		return amount;
	}

	public void setamount(Double amount) {
		this.amount = amount;
	}

	public String getcategory() {
		return category;
	}

	public void setcategory(String category) {
		this.category = category;
	}

	public Boolean getstatus() {
		return status;
	}

	public void setstatus(Boolean status) {
		this.status = status;
	}

	public String getcreationDate() {
		return creationDate;
	}

	public void setcreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getexpirationDate() {
		return expirationDate;
	}

	public void setexpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getuserId() {
		return userId;
	}

	public void setuserId(String userId) {
		this.userId = userId;
	}

}

