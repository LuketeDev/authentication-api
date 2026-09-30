package com.lukete.authentication_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lukete.authentication_api.domain.Role;
import com.lukete.authentication_api.domain.User;
import com.lukete.authentication_api.dto.RegisterUserRequest;
import com.lukete.authentication_api.exception.EmailAlreadyRegisteredException;
import com.lukete.authentication_api.repository.UserRepository;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterUser() {
        RegisterUserRequest request = defaultRegisterRequest();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("hashed_password");

        User savedUser = new User();
        savedUser.setEmail(request.email());
        savedUser.setPasswordHash("hashed_password");
        savedUser.setRole(Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(request);

        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getPasswordHash()).isEqualTo("hashed_password");
        assertThat(result.getRole()).isEqualTo(Role.USER);

        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldStoreHashedPasswordInsteadOfOriginalPassword() {
        RegisterUserRequest request = defaultRegisterRequest();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(request);

        assertThat(result.getPasswordHash()).isEqualTo("hashed_password").isNotEqualTo("password123");

        verify(passwordEncoder).encode(request.password());
    }

    @Test
    void shouldRejectAlreadyRegisteredEmail() {
        RegisterUserRequest request = defaultRegisterRequest();
        User existing = new User();
        existing.setEmail(request.email());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyRegisteredException.class)
                .hasMessage("Email already registered");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    private RegisterUserRequest defaultRegisterRequest() {
        return new RegisterUserRequest("test@example.com", "password123");
    }
}
