package com.example.shopapp.services.impls;

import com.example.shopapp.components.JwtTokenUtils;
import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.dtos.UserLoginDTO;
import com.example.shopapp.dtos.UserRegisterDTO;
import com.example.shopapp.exceptionhandling.DataNotFoundException;
import com.example.shopapp.exceptionhandling.ExpiredTokenException;
import com.example.shopapp.exceptionhandling.PermissionDeniedException;
import com.example.shopapp.models.Role;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.RoleRepository;
import com.example.shopapp.repositories.UserRepository;
import com.example.shopapp.services.UserService;
import com.example.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtils jwtTokenUtils;

    private final AuthenticationManager authenticationManager;

    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public User createUser(UserRegisterDTO registerDTO) {
        if(userRepository.existsByPhoneNumber(registerDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PHONE_EXISTED)
            );
        }
        // check role
        Role role = roleRepository.findById(registerDTO.getRoleId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.ROLE_NOT_FOUND))
                );
        if(role.getName().equalsIgnoreCase(Role.ADMIN)) {
            throw new PermissionDeniedException(
                    localizationUtils.getLocalizedMessage(MessageKeys.ROLE_NOT_ALLOWED, Role.ADMIN)
            );
        }
        User newUser = User.builder()
                .fullName(registerDTO.getFullName())
                .phoneNumber(registerDTO.getPhoneNumber())
                .address(registerDTO.getAddress())
                .isActive(true)
                .dateOfBirth(registerDTO.getDateOfBirth())
                .facebookAccountId(registerDTO.getFacebookAccountId())
                .googleAccountId(registerDTO.getGoogleAccountId())
                .build();

        newUser.setRole(role);
        // Check if not register with social account
        if(newUser.getFacebookAccountId() == 0 || newUser.getGoogleAccountId() == 0) {
            String rawPassword = registerDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            newUser.setPassword(encodedPassword);
        }

        return userRepository.save(newUser);
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        User existingUser = userRepository.findByPhoneNumber(loginDTO.getPhoneNumber())
                .orElseThrow(() -> new BadCredentialsException(
                        localizationUtils.getLocalizedMessage(MessageKeys.INVALID_CREDENTIALS)
                ));
        // Check if not register with social account -> require to check password
        if(existingUser.getFacebookAccountId() == 0 || existingUser.getGoogleAccountId() == 0) {
            if(!passwordEncoder.matches(loginDTO.getPassword(), existingUser.getPassword())) {
                throw new BadCredentialsException(
                        localizationUtils.getLocalizedMessage(MessageKeys.INVALID_CREDENTIALS)
                );
            }
        }
        Optional<Role> optionalRole = roleRepository.findById(loginDTO.getRoleId());
        if(optionalRole.isEmpty() || !loginDTO.getRoleId().equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.ROLE_NOT_FOUND)
            );
        }

        if(!existingUser.isActive()) {
            throw new PermissionDeniedException(
                    localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED)
            );
        }

        // authenticate with Java Spring Security
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getPhoneNumber(),
                loginDTO.getPassword(),
                existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authToken);

        // return JWT token
        return jwtTokenUtils.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) {
        if(jwtTokenUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        User existingUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND_DEFAULT)
                ));

        return existingUser;
    }

    @Override
    @Transactional
    public User updateUserDetails(Long userId, UserDTO userDTO) {
        // check user
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId)
                ));

        // check phone
        String newPhoneNumber = userDTO.getPhoneNumber() != null ? userDTO.getPhoneNumber() : "";
        if(!newPhoneNumber.equals(existingUser.getPhoneNumber()) && userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PHONE_EXISTED)
            );
        } else if(!newPhoneNumber.isEmpty()) {
            existingUser.setPhoneNumber(newPhoneNumber);
        }

        // check role
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.ROLE_NOT_FOUND))
                );
        if(role.getName().equalsIgnoreCase(Role.ADMIN)) {
            throw new PermissionDeniedException("You are not allowed to change to ADMIN");
        }
        // existingUser.setRole(role);

        if(userDTO.getFullName() != null && !userDTO.getFullName().isEmpty()) {
            existingUser.setFullName(userDTO.getFullName());
        }
        if(userDTO.getAddress() != null && !userDTO.getAddress().isEmpty()) {
            existingUser.setAddress(userDTO.getAddress());
        }
        if(userDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userDTO.getDateOfBirth());
        }
        if(userDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(userDTO.getFacebookAccountId());
        }
        if(userDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(userDTO.getGoogleAccountId());
        }

        // encode and update password
        String newPassword = userDTO.getPassword();
        if(newPassword != null && newPassword.length() >= 3) {
            // check password and retype password mismatch
            if(!newPassword.equals(userDTO.getRetypePassword())) {
                throw new DataIntegrityViolationException("Password and retype password not match");
            }

            String rawPassword = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            existingUser.setPassword(encodedPassword);
        }
        return userRepository.save(existingUser);
    }
}
