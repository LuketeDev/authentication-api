package com.lukete.authentication_api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.lukete.authentication_api.TestcontainersConfiguration;
import com.lukete.authentication_api.domain.Role;
import com.lukete.authentication_api.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUser() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                            "email": "integration@example.com",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        var user = userRepository.findByEmail("integration@example.com").orElseThrow();

        assertThat(user.getEmail()).isEqualTo("integration@example.com");
        assertThat(user.getPasswordHash()).isNotEqualTo("password123");
        assertThat(user.getPasswordHash()).startsWith("$2");
        assertThat(user.getRole()).isEqualTo(Role.USER);

    }
}
