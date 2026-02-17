package com.example.marketplace.model;

public enum OrderStatus {

    PAID("Paid"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    ;

    private final String status;
    OrderStatus(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
