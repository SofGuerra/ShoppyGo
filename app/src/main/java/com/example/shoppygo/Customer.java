package com.example.shoppygo;

import java.util.ArrayList;

public class Customer extends User {
    private String name;
    private String address;
    private ArrayList<CartProduct> cartItems;
    private ArrayList<String> orders;
    private ArrayList<String> paymentMethods;

    public Customer() {
        super();
        this.cartItems = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.paymentMethods = new ArrayList<>();
    }

    public Customer(String id, String email, String name, String address) {
        super(id, email, "customer");
        this.name = name;
        this.address = address;
        this.cartItems = new ArrayList<>();
        cartItems.add(new CartProduct("test", 3));
        this.orders = new ArrayList<>();
        this.paymentMethods = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<CartProduct> getCartItems() {
        return cartItems;
    }

    public void setCartItems(ArrayList<CartProduct> cartItems) {
        this.cartItems = cartItems;
    }

    public ArrayList<String> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<String> orders) {
        this.orders = orders;
    }

    public ArrayList<String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(ArrayList<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public void updateCartQty(String productId, int qty) {
        for (CartProduct item : cartItems) {
            if (item.getProductId().equals(productId)) {
                item.setQty(qty);
                break;
            }
        }
    }

    public void removeCartProduct(String productId) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProductId().equals(productId)) {
                cartItems.remove(i);
                break;
            }
        }
    }

}