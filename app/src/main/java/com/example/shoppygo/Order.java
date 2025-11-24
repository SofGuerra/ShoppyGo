package com.example.shoppygo;


import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    private String id;
    private Long date;
    private List<CartProduct> items;
    private String customerName;
    private String address;
    private String customerId;

    public Order() {

    }

    public Order(String id, String customerId, Long date, List<CartProduct> items, String customerName, String address) {
        this.id = id;
        this.date = date;
        this.items = items;
        this.customerName = customerName;
        this.customerId = customerId;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<CartProduct> getItems() {
        return items;
    }

    public void setItems(List<CartProduct> items) {
        this.items = items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}