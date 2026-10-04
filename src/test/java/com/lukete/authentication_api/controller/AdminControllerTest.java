package com.lukete.authentication_api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.lukete.authentication_api.TestcontainersConfiguration;
import com.lukete.authentication_api.domain.Role;
import com.lukete.authentication_api.domain.User;
import com.lukete.authentication_api.repository.UserRepository;
import com.lukete.authentication_api.service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRejectUnauthenticatedRequest() throws Exception {
        mvc.perform(get("/api/v1/admin/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRegularUser() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash("hashed-password");

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        mvc.perform(get("/api/v1/admin/test")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAcceptAdminUser() throws Exception {
        User user = new User();
        user.setEmail("admin@example.com");
        user.setPasswordHash("hashed-password");
        user.setRole(Role.ADMIN);

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        mvc.perform(get("/api/v1/admin/test")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}