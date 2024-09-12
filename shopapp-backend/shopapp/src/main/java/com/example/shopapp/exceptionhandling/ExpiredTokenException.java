package com.example.shopapp.exceptionhandling;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(String message) {
        super(message);
    }
}
