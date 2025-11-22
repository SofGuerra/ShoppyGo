package com.example.shoppygo;

import java.io.Serializable;

public class CartProduct implements Serializable {

    private String productId;
    private int qty;

    public CartProduct() {
    }

    public CartProduct(String productId, int qty) {
        this.productId = productId;
        this.qty = qty;
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
}
