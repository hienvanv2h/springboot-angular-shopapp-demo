package com.example.shopapp.exceptionhandling;

public class InvalidParamException extends RuntimeException {

    public InvalidParamException(String message) {
        super(message);
    }
}
