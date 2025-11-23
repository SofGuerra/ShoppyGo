package com.example.shoppygo;

public class Order {

    private String orderId;
    private String ordproductId;
    private String ordimageUrl;
    private String ordreference;
    private String size;
    private String color;
    private String deliverBy;

    private String shippingAddress;
    private String customerName;
    private String shipBy;

    public Order(){

    }

    public Order(String customerName, String shippingAddress, String shipBy, String deliverBy, String color, String size, String ordreference, String ordimageUrl, String ordproductId, String orderId) {
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
        this.shipBy = shipBy;
        this.deliverBy = deliverBy;
        this.color = color;
        this.size = size;
        this.ordreference = ordreference;
        this.ordimageUrl = ordimageUrl;
        this.ordproductId = ordproductId;
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrdproductId() {
        return ordproductId;
    }

    public void setOrdproductId(String ordproductId) {
        this.ordproductId = ordproductId;
    }

    public String getOrdimageUrl() {
        return ordimageUrl;
    }

    public void setOrdimageUrl(String ordimageUrl) {
        this.ordimageUrl = ordimageUrl;
    }

    public String getOrdreference() {
        return ordreference;
    }

    public void setOrdreference(String ordreference) {
        this.ordreference = ordreference;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDeliverBy() {
        return deliverBy;
    }

    public void setDeliverBy(String deliverBy) {
        this.deliverBy = deliverBy;
    }

    public String getShipBy() {
        return shipBy;
    }

    public void setShipBy(String shipBy) {
        this.shipBy = shipBy;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
