package com.lukete.authentication_api.dto;

import java.time.Instant;
import java.util.UUID;

import com.lukete.authentication_api.domain.Role;

public record UserResponse(UUID id, String email, Role role, Instant createdAt, Instant updatedAt) {
}
