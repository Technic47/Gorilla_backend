package ru.gorilla.gim.backend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.gorilla.gim.backend.BaseIntegrationTest;
import ru.gorilla.gim.backend.dto.AuthResponse;
import ru.gorilla.gim.backend.dto.ChangeCredentialsRequest;
import ru.gorilla.gim.backend.dto.LoginRequest;
import ru.gorilla.gim.backend.entity.UserEntity;
import ru.gorilla.gim.backend.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.gorilla.gim.backend.util.Role.ROLE_ADMIN;

class UserControllerIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_USERNAME = "test-user-credentials";
    private static final String TEST_PASSWORD = "test-pass-456";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        userRepository.findByUsername(TEST_USERNAME).ifPresent(userRepository::delete);

        UserEntity user = new UserEntity();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setRole(ROLE_ADMIN);
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        testUserId = userRepository.save(user).getId();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteById(testUserId);
    }

    private String obtainTestUserToken() {
        AuthResponse response = restTestClient
                .post()
                .uri("/auth/login")
                .body(new LoginRequest(TEST_USERNAME, TEST_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        return response.token();
    }

    // ── changeCredentials ─────────────────────────────────────────────────────

    @Test
    void changeCredentials_newUsername_returnsNewToken() {
        AuthResponse response = restTestClient
                .patch()
                .uri("/users/me/credentials")
                .header("Authorization", "Bearer " + obtainTestUserToken())
                .body(new ChangeCredentialsRequest(TEST_PASSWORD, "changed-username", null))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void changeCredentials_newPassword_allowsLoginWithNewPassword() {
        String newPassword = "updated-pass-789";

        restTestClient
                .patch()
                .uri("/users/me/credentials")
                .header("Authorization", "Bearer " + obtainTestUserToken())
                .body(new ChangeCredentialsRequest(TEST_PASSWORD, null, newPassword))
                .exchange()
                .expectStatus().isOk();

        restTestClient
                .post()
                .uri("/auth/login")
                .body(new LoginRequest(TEST_USERNAME, newPassword))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void changeCredentials_bothFields_returnsNewToken() {
        AuthResponse response = restTestClient
                .patch()
                .uri("/users/me/credentials")
                .header("Authorization", "Bearer " + obtainTestUserToken())
                .body(new ChangeCredentialsRequest(TEST_PASSWORD, "changed-both-user", "changed-both-pass"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void changeCredentials_wrongCurrentPassword_returnsUnauthorized() {
        restTestClient
                .patch()
                .uri("/users/me/credentials")
                .header("Authorization", "Bearer " + obtainTestUserToken())
                .body(new ChangeCredentialsRequest("wrong-password", "new-name", null))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void changeCredentials_duplicateUsername_returnsConflict() {
        restTestClient
                .patch()
                .uri("/users/me/credentials")
                .header("Authorization", "Bearer " + obtainTestUserToken())
                .body(new ChangeCredentialsRequest(TEST_PASSWORD, ADMIN_USERNAME, null))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void changeCredentials_noToken_returnsUnauthorized() {
        restTestClient
                .patch()
                .uri("/users/me/credentials")
                .body(new ChangeCredentialsRequest(TEST_PASSWORD, "new-name", null))
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
