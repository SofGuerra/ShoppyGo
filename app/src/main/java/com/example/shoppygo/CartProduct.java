package com.example.shoppygo;

import java.io.Serializable;

public class CartProduct implements Serializable {

    private String productId;
    private int qty;
    private String color;
    private String size;

    public CartProduct() {
    }

    public CartProduct(String productId, int qty, String color, String size) {
        this.productId = productId;
        this.qty = qty;
        this.color = color;
        this.size = size;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
