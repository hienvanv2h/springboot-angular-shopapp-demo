package com.example.shopapp.services;

import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.dtos.UserLoginDTO;
import com.example.shopapp.dtos.UserRegisterDTO;
import com.example.shopapp.models.User;

public interface UserService {

    User createUser(UserRegisterDTO registerDTO);

    String login(UserLoginDTO loginDTO);

    User getUserDetailsFromToken(String token);

    User updateUserDetails(Long userId, UserDTO userDTO);
}
