package com.example.shopapp.responses.user;

import com.example.shopapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private User user;

    private String message;

    private List<String> errors;
}
