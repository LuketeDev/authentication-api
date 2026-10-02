package com.lukete.authentication_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lukete.authentication_api.domain.Role;
import com.lukete.authentication_api.domain.User;
import com.lukete.authentication_api.dto.LoginRequest;
import com.lukete.authentication_api.dto.LoginResponse;
import com.lukete.authentication_api.dto.RegisterUserRequest;
import com.lukete.authentication_api.exception.EmailAlreadyRegisteredException;
import com.lukete.authentication_api.exception.InvalidCredentialsException;
import com.lukete.authentication_api.exception.UserNotFoundException;
import com.lukete.authentication_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User register(RegisterUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Password does not match");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }
}