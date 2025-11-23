package com.example.shoppygo;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    private String id;
    private Long date;
    private List<CartProduct> items;
    private String name;
    private String address;

    public Order() {

    }

    public Order(String id, Long date, List<CartProduct> items, String name, String address) {
        this.id = id;
        this.date = date;
        this.items = items;
        this.name = name;
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
}