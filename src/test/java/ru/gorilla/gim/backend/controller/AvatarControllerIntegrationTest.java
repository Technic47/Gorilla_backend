package ru.gorilla.gim.backend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.gorilla.gim.backend.BaseIntegrationTest;
import ru.gorilla.gim.backend.dto.FileMetaDto;
import ru.gorilla.gim.backend.entity.FileMetaEntity;
import ru.gorilla.gim.backend.repository.FileMetadataRepository;
import ru.gorilla.gim.backend.service.MinoService;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AvatarControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @MockitoBean
    private MinoService minoService;

    private String adminToken;
    private Long createdId;

    @BeforeEach
    void setUp() {
        adminToken = obtainAdminToken();

        FileMetaEntity entity = new FileMetaEntity();
        entity.setOriginalName("test-avatar.jpg");
        entity.setObjectKey("uploads/test-key-001");
        entity.setContentType("image/jpeg");
        entity.setStatus("PENDING");
        entity.setCreated(LocalDateTime.now());
        entity.setUpdated(LocalDateTime.now());

        createdId = fileMetadataRepository.save(entity).getId();
    }

    @AfterEach
    void tearDown() {
        if (createdId != null) {
            fileMetadataRepository.findById(createdId).ifPresent(fileMetadataRepository::delete);
            createdId = null;
        }
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void findAll_authenticated_returnsListWithCreatedRecord() {
        FileMetaDto[] response = restTestClient
                .get()
                .uri("/avatar")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FileMetaDto[].class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response).anyMatch(f -> createdId.equals(f.getId()));
    }

    @Test
    void findAll_unauthenticated_returnsUnauthorized() {
        restTestClient
                .get()
                .uri("/avatar")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ── findPage ──────────────────────────────────────────────────────────────

    @Test
    void findPage_defaultParams_returnsPageWithCreatedRecord() {
        restTestClient
                .get()
                .uri("/avatar/page")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[?(@.id == " + createdId + ")]").exists()
                .jsonPath("$.totalElements").isNumber()
                .jsonPath("$.size").isEqualTo(10)
                .jsonPath("$.number").isEqualTo(0);
    }

    @Test
    void findPage_customSizeAndPage_returnsCorrectPage() {
        restTestClient
                .get()
                .uri("/avatar/page?page=0&size=1")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.size").isEqualTo(1)
                .jsonPath("$.number").isEqualTo(0);
    }

    @Test
    void findPage_unauthenticated_returnsUnauthorized() {
        restTestClient
                .get()
                .uri("/avatar/page")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_existingRecord_returnsRecord() {
        FileMetaDto response = restTestClient
                .get()
                .uri("/avatar/" + createdId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FileMetaDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getOriginalName()).isEqualTo("test-avatar.jpg");
        assertThat(response.getObjectKey()).isEqualTo("uploads/test-key-001");
        assertThat(response.getContentType()).isEqualTo("image/jpeg");
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void findById_nonExistentId_returnsNotFound() {
        restTestClient
                .get()
                .uri("/avatar/999999999")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_existingRecord_returnsUpdatedData() {
        FileMetaDto updated = new FileMetaDto("updated-avatar.png", "uploads/updated-key", "image/png", "UPLOADED");

        FileMetaDto response = restTestClient
                .put()
                .uri("/avatar/" + createdId)
                .header("Authorization", "Bearer " + adminToken)
                .body(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FileMetaDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getOriginalName()).isEqualTo("updated-avatar.png");
        assertThat(response.getStatus()).isEqualTo("UPLOADED");
    }

    // ── patchUpdate ───────────────────────────────────────────────────────────

    @Test
    void patchUpdate_singleField_returnsUpdatedField() {
        FileMetaDto response = restTestClient
                .patch()
                .uri("/avatar/" + createdId)
                .header("Authorization", "Bearer " + adminToken)
                .body(Map.of("status", "UPLOADED"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(FileMetaDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("UPLOADED");
        assertThat(response.getOriginalName()).isEqualTo("test-avatar.jpg");
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existingRecord_returnsNoContent() {
        restTestClient
                .delete()
                .uri("/avatar/" + createdId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();

        createdId = null;
    }

    @Test
    void deleteById_nonExistentId_returnsNotFound() {
        restTestClient
                .delete()
                .uri("/avatar/999999999999")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }

    // ── registerUpload ────────────────────────────────────────────────────────

    @Test
    void registerUpload_validAccount_returnsPresignedUrl() {
        when(minoService.getPreSignedUploadUrlForAvatar(anyString()))
                .thenReturn("https://minio.example.com/presigned-url");

        Long accountId = createTestAccount();
        try {
            var request = Map.of(
                    "accountId", accountId,
                    "fileName", "avatar.jpg",
                    "contentType", "image/jpeg"
            );

            restTestClient
                    .post()
                    .uri("/avatar/register-upload")
                    .header("Authorization", "Bearer " + adminToken)
                    .body(request)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.url").isEqualTo("https://minio.example.com/presigned-url")
                    .jsonPath("$.objectKey").isNotEmpty();
        } finally {
            cleanupTestAccount(accountId);
        }
    }

    @Test
    void registerUpload_nonExistentAccount_returnsBadRequest() {
        restTestClient
                .post()
                .uri("/avatar/register-upload")
                .header("Authorization", "Bearer " + adminToken)
                .body(Map.of("accountId", 999999999L, "fileName", "avatar.jpg", "contentType", "image/jpeg"))
                .exchange()
                .expectStatus().isBadRequest();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Long createTestAccount() {
        ru.gorilla.gim.backend.dto.AccountDto dto = new ru.gorilla.gim.backend.dto.AccountDto();
        dto.setFirstName("Avatar");
        dto.setLastName("Test");
        dto.setCardNumber("AVATAR-TEST-001");
        dto.setIsBlocked(false);

        ru.gorilla.gim.backend.dto.AccountDto response = restTestClient
                .post()
                .uri("/account")
                .header("Authorization", "Bearer " + adminToken)
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ru.gorilla.gim.backend.dto.AccountDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        return response.getId();
    }

    private void cleanupTestAccount(Long id) {
        if (id != null) {
            restTestClient
                    .delete()
                    .uri("/account/" + id)
                    .header("Authorization", "Bearer " + adminToken)
                    .exchange();
        }
    }
}
