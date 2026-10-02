package com.lukete.authentication_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lukete.authentication_api.dto.LoginRequest;
import com.lukete.authentication_api.dto.LoginResponse;
import com.lukete.authentication_api.dto.RegisterUserRequest;
import com.lukete.authentication_api.dto.UserResponse;
import com.lukete.authentication_api.mapper.UserMapper;
import com.lukete.authentication_api.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "Operations for user authentication")
@RequestMapping(path = "/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        UserResponse response = userMapper.toResponse(userService.register(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

}
