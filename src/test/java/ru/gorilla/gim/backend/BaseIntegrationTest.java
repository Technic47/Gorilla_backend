package ru.gorilla.gim.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.gorilla.gim.backend.dto.AuthResponse;
import ru.gorilla.gim.backend.dto.LoginRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "admin.username=test-admin",
                "admin.password=test-password123"
        }
)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureRestTestClient
public abstract class BaseIntegrationTest {

    protected static final String ADMIN_USERNAME = "test-admin";
    protected static final String ADMIN_PASSWORD = "test-password123";

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("GorillaTest")
            .withUsername("postgres")
            .withPassword("testtesttest");

    static final MinIOContainer minio = new MinIOContainer("minio/minio:latest");

    static {
        postgres.start();
        minio.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("minio.url", minio::getS3URL);
        registry.add("minio.username", minio::getUserName);
        registry.add("minio.password", minio::getPassword);
    }

    @Autowired
    protected RestTestClient restTestClient;

    protected String obtainAdminToken() {
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
        return response.token();
    }
}
