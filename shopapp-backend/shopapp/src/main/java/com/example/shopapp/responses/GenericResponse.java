package com.example.shopapp.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {

    private Object data;

    private String message;

    private List<String> errors;

    public static GenericResponse getErrorMessages(List<String> errorMessages) {
        return GenericResponse.builder()
                .errors(errorMessages)
                .build();
    }
}
