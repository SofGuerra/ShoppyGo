package com.example.shoppygo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    private String id;
    private String name;
    private String productRef;
    private Double price;
    private String imageURL;
    private List<String> color;
    private List<String> itemsize;

    public Product(){

    }

    public Product (String id, String n, String r, Double p, String url, List<String> c, List<String> sz){
        this.id = id;
        name = n;
        productRef = r;
        price = p;
        imageURL = url;
        color = c;
        itemsize = sz;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductRef() {
        return productRef;
    }

    public void setProductRef(String productRef) {
        this.productRef = productRef;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getColor() {
        return color;
    }

    public void setColor(List<String> color) {
        this.color = color;
    }

    public List<String> getitemsize() {
        return itemsize;
    }

    public void setitemsize(List<String> itsize) {
        itemsize = itsize;
    }

}
