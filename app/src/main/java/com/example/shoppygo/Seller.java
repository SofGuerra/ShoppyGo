package com.example.shoppygo;

public class Seller extends User {
    private String companyName;
    private String productLine;

    public Seller() {
        super();
    }

    public Seller(String id, String email, String companyName) {
        super(id, email, "seller");
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProductLine() {
        return productLine;
    }

    public void setProductLine(String productLine) {
        this.productLine = productLine;
    }
}
