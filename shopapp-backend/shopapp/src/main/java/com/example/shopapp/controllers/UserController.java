package com.example.shopapp.controllers;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.dtos.UserLoginDTO;
import com.example.shopapp.dtos.UserRegisterDTO;
import com.example.shopapp.models.User;
import com.example.shopapp.responses.GenericResponse;
import com.example.shopapp.responses.user.LoginResponse;
import com.example.shopapp.responses.user.RegisterResponse;
import com.example.shopapp.responses.user.UserDetailsResponse;
import com.example.shopapp.services.UserService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final LocalizationUtils localizationUtils;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserRegisterDTO registerDTO,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    RegisterResponse.builder()
                            .errors(errorMessages)
                            .build()
            );
        }
        // Check if password match
        if(!registerDTO.getPassword().equals(registerDTO.getRetypePassword())) {
            return ResponseEntity.badRequest().body(
                    RegisterResponse.builder()
                            .errors(List.of(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_MISMATCH)))
                            .build()
            );
        }
        User savedUser = userService.createUser(registerDTO);
        return ResponseEntity.ok(
                RegisterResponse.builder()
                        .user(savedUser)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESS))
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginDTO loginDTO,
            BindingResult result,
            HttpServletRequest request
    ) {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .errors(errorMessages)
                            .build()
            );
        }
        // Validate user credentials and generate token sent back to user
        var token = userService.login(loginDTO);
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .message(localizationUtils
                                .getLocalizedMessage(MessageKeys.LOGIN_SUCCESS))
                        .token(token)
                        .build()
        );
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    GenericResponse.getErrorMessages(
                            List.of(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_TOKEN_FORMAT)))
            );
        }
        String extractedToken = authHeader.substring(7);     // remove "Bearer " prefix
        User user = userService.getUserDetailsFromToken(extractedToken);
        return ResponseEntity.ok().body(UserDetailsResponse.fromUser(user));
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<?> updateUserDetails(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long userId,
            @RequestBody UserDTO userDTO) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    GenericResponse.getErrorMessages(
                            List.of(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_TOKEN_FORMAT)))
            );
        }
        String extractedToken = authHeader.substring(7);     // remove "Bearer " prefix
        User user = userService.getUserDetailsFromToken(extractedToken);
        if(!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User updatedUser = userService.updateUserDetails(userId, userDTO);
        return ResponseEntity.ok().body(UserDetailsResponse.fromUser(updatedUser));
    }

    @ExceptionHandler(value = {
            DataIntegrityViolationException.class,
            BadCredentialsException.class,
    })
    public ResponseEntity<?> handleCredentialErrors(Exception ex) {
        LoginResponse response = LoginResponse.builder()
                .errors(List.of(ex.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
