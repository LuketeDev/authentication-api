package com.lukete.authentication_api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lukete.authentication_api.domain.User;
import com.lukete.authentication_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException()); // todo create exception
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException()); // todo create exception
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }
}
