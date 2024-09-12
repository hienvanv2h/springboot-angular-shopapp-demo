package com.example.shopapp.advice;

import com.example.shopapp.exceptionhandling.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
public class AppControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            DataNotFoundException.class,
            InvalidParamException.class,
    })
    public ResponseEntity<ErrorInfo> handleDefinedClientException(Exception exc) {
        return handleException(exc, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            PermissionDeniedException.class,
            ExpiredTokenException.class,
    })
    public ResponseEntity<ErrorInfo> handlePermissionDeniedException(Exception exc) {
        return handleException(exc, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorInfo> unknownExceptionHandler(Exception exc) {
        return handleException(exc, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<ErrorInfo> handleException(Exception exc, HttpStatus httpStatus) {
        var errorMessage = ErrorInfo.builder()
                .timestamp(Instant.now())
                .status(httpStatus.value())
                .message(exc.getClass().getSimpleName() + ": " + exc.getMessage())
                .build();
        return new ResponseEntity<>(errorMessage, httpStatus);
    }
}
