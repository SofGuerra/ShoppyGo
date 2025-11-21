package com.example.shoppygo;

public class Seller {
    private String sellerId;
    private String Companyname;
    private String email;
    private String productLine;

    public Seller(String sellerId, String companyname, String email) {
        this.sellerId = sellerId;
        Companyname = companyname;
        this.email = email;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getCompanyname() {
        return Companyname;
    }

    public void setCompanyname(String cname) {
        this.Companyname = cname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
