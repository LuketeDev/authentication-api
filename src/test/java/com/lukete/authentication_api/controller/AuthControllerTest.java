package com.lukete.authentication_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lukete.authentication_api.domain.Role;
import com.lukete.authentication_api.domain.User;
import com.lukete.authentication_api.dto.LoginRequest;
import com.lukete.authentication_api.dto.LoginResponse;
import com.lukete.authentication_api.dto.RegisterUserRequest;
import com.lukete.authentication_api.dto.UserResponse;
import com.lukete.authentication_api.exception.EmailAlreadyRegisteredException;
import com.lukete.authentication_api.exception.InvalidCredentialsException;
import com.lukete.authentication_api.mapper.UserMapper;
import com.lukete.authentication_api.service.JwtService;
import com.lukete.authentication_api.service.UserService;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRegisterUser() throws Exception {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        RegisterUserRequest request = new RegisterUserRequest("test@example.com", "password123");

        User user = new User();
        user.setId(id);
        user.setEmail(request.email());
        user.setRole(Role.USER);

        UserResponse response = new UserResponse(id, "test@example.com", Role.USER, now, now);

        when(userService.register(any(RegisterUserRequest.class)))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "invalid-email",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectShortPassword() throws Exception {

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "1234567"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenEmailIsAlreadyRegistered() throws Exception {
        when(userService.register(any(RegisterUserRequest.class)))
                .thenThrow(new EmailAlreadyRegisteredException("Email already registered"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "password123");

        LoginResponse response = new LoginResponse("jwt-token");

        when(userService.login(request))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email": "test@example.com",
                                "password": "password123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(userService).login(request);
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "wrong-password");

        when(userService.login(request)).thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "wrong-password"
                        }
                        """))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @ParameterizedTest
    @CsvSource({
            "'invalid-email', 'password123'",
            "'test@example.com', '123'"
    })
    void shouldRejectInvalidLoginRequest(String email, String password) throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email": "%s",
                                "password": "%s"
                            }
                        """.formatted(email, password)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).login(any(LoginRequest.class));
    }
}