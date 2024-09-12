package com.example.shopapp.exceptionhandling;

import lombok.*;

import java.time.Instant;

@Builder
@Data
public class ErrorInfo {
    Instant timestamp;
    private int status;
    private String message;
}

