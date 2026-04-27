package ru.gorilla.gim.backend.controller;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import ru.gorilla.gim.backend.BaseIntegrationTest;
import ru.gorilla.gim.backend.dto.AuthResponse;
import ru.gorilla.gim.backend.dto.LoginRequest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(1)
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsToken() {
        AuthResponse response = restTestClient
                .post()
                .uri("/auth/login")
                .body(new LoginRequest(ADMIN_USERNAME, ADMIN_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void login_wrongPassword_returnsUnauthorized() {
        restTestClient
                .post()
                .uri("/auth/login")
                .body(new LoginRequest(ADMIN_USERNAME, "wrong-password"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void login_nonExistentUser_returnsUnauthorized() {
        restTestClient
                .post()
                .uri("/auth/login")
                .body(new LoginRequest("ghost", "wrong-password"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ── validate ──────────────────────────────────────────────────────────────

    @Test
    void validate_validToken_returnsOk() {
        restTestClient
                .get()
                .uri("/auth/validate")
                .header("Authorization", "Bearer " + obtainAdminToken())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void validate_noToken_returnsUnauthorized() {
        restTestClient
                .post()
                .uri("/auth/validate")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void validate_invalidToken_returnsUnauthorized() {
        restTestClient
                .get()
                .uri("/auth/validate")
                .header("Authorization", "invalid.jwt.token")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ── refresh ───────────────────────────────────────────────────────────────

    @Test
    void refresh_validToken_returnsNewToken() throws InterruptedException {
        String token = obtainAdminToken();

        Thread.sleep(Duration.ofSeconds(3).toMillis()); //Метод слишком быстро делает запросы. Токен обновляется на то же время и тест обваливается.

        AuthResponse response = restTestClient
                .post()
                .uri("/auth/refresh")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.token()).isNotBlank();
        assertThat(token).isNotEqualTo(response.token());
    }

    @Test
    void refresh_invalidToken_returnsUnauthorized() {
        restTestClient
                .get()
                .uri("/auth/refresh")
                .header("Authorization", "invalid.jwt.token")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
