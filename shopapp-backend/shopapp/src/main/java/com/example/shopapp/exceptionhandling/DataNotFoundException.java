package com.example.shopapp.exceptionhandling;

public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String message) {
        super(message);
    }
}
