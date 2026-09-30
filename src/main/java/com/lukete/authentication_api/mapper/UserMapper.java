package com.lukete.authentication_api.mapper;

import org.springframework.stereotype.Component;

import com.lukete.authentication_api.domain.User;
import com.lukete.authentication_api.dto.UserResponse;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
