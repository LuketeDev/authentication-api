package com.lukete.authentication_api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.lukete.authentication_api.TestcontainersConfiguration;
import com.lukete.authentication_api.domain.Role;
import com.lukete.authentication_api.domain.User;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserById() {
        User user = createUser();
        userRepository.save(user);
        User found = userRepository.findById(user.getId()).orElseThrow();
        assertUserEquals(user, found);
    }

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = createUser();
        userRepository.save(user);
        User found = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertUserEquals(user, found);
    }

    private User createUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setPasswordHash("test_hash");
        return user;
    }

    private void assertUserEquals(User expected, User actual) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getRole()).isEqualTo(expected.getRole());
        assertThat(actual.getPasswordHash()).isEqualTo(expected.getPasswordHash());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
    }

}
