package com.lukete.authentication_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Users", description = "Operations for managing users")
@RequestMapping(path = "/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<Void> me() {
        return ResponseEntity.ok().build();
    }

}
