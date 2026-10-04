package com.lukete.authentication_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.lukete.authentication_api.TestcontainersConfiguration;
import com.lukete.authentication_api.domain.User;
import com.lukete.authentication_api.repository.UserRepository;
import com.lukete.authentication_api.service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldRejectUnauthenticatedRequest() throws Exception {
        mvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAcceptAuthorizedUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed-password");

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        mvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldReturnNotFoundWhenAuthenticatedUserDoesNotExist() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("ghost@example.com");
        user.setPasswordHash("hashed-password");

        String token = jwtService.generateToken(user);

        mvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"));
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        mvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithInvalidAuthorizationScheme() throws Exception {
        mvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Basic some-token"))
                .andExpect(status().isUnauthorized());
    }
}
