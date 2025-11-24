package com.example.shoppygo;

import java.util.ArrayList;

public class Seller extends User {
    private String companyName;
    private String productLine;
    private ArrayList<String> products = new ArrayList<>();

    public Seller() {
        super();
    }

    public Seller(String id, String email, String companyName) {
        super(id, email, "seller");
        this.companyName = companyName;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
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
