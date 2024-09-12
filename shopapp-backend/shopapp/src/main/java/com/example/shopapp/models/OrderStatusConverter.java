package com.example.shopapp.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        return orderStatus.getStatus();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String s) {
        return OrderStatus.fromString(s);
    }
}
