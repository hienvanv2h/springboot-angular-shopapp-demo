package com.example.shopapp.models;

import lombok.Getter;

import java.util.Arrays;

public enum OrderStatus {

    PENDING("pending"),
    PROCESSING("processing"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    CANCELED("canceled");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static OrderStatus fromString(String s) {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status.getStatus().equals(s))
                .findFirst()
                .orElse(PENDING);
    }
}
