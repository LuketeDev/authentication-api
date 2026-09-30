package com.lukete.authentication_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lukete.authentication_api.domain.Role;
import com.lukete.authentication_api.domain.User;
import com.lukete.authentication_api.dto.RegisterUserRequest;
import com.lukete.authentication_api.exception.EmailAlreadyRegisteredException;
import com.lukete.authentication_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
