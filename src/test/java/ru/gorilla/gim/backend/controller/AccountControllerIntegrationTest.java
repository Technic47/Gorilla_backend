package ru.gorilla.gim.backend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gorilla.gim.backend.BaseIntegrationTest;
import ru.gorilla.gim.backend.dto.AccountDto;
import ru.gorilla.gim.backend.repository.AccountRepository;
import ru.gorilla.gim.backend.util.CommonUnits;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AccountControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    private String adminToken;
    private Long createdId;

    @BeforeEach
    void setUp() {
        adminToken = obtainAdminToken();

        AccountDto response = restTestClient
                .post()
                .uri("/account")
                .header("Authorization", "Bearer " + adminToken)
                .body(testAccount())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        createdId = response.getId();
    }

    @AfterEach
    void tearDown() {
        if (createdId != null) {
            accountRepository.findById(createdId).ifPresent(accountRepository::delete);
            createdId = null;
        }
    }

    // ── findPage ──────────────────────────────────────────────────────────────

    @Test
    void findPage_defaultParams_returnsPageWithCreatedAccount() {
        restTestClient
                .get()
                .uri("/account/page")
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
                .uri("/account/page?page=0&size=1")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.size").isEqualTo(1)
                .jsonPath("$.number").isEqualTo(0);
    }

    @Test
    void findPage_sortByLastName_returnsSortedContent() {
        restTestClient
                .get()
                .uri("/account/page?sort=lastName,asc")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray();
    }

    @Test
    void findPage_unauthenticated_returnsUnauthorized() {
        restTestClient
                .get()
                .uri("/account/page")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void findAll_authenticated_returnsListWithCreatedAccount() {
        AccountDto[] response = restTestClient
                .get()
                .uri("/account")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountDto[].class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response).anyMatch(a -> createdId.equals(a.getId()));
    }

    @Test
    void findAll_unauthenticated_returnsUnauthorized() {
        restTestClient
                .get()
                .uri("/account")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_existingAccount_returnsAccount() {
        AccountDto response = restTestClient
                .get()
                .uri("/account/" + createdId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Integration");
        assertThat(response.getLastName()).isEqualTo("Test");
        assertThat(response.getCardNumber()).isEqualTo("TEST-CARD-001");
    }

    @Test
    void findById_nonExistentId_returnsNotFound() {
        restTestClient
                .get()
                .uri("/account/999999999")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_existingAccount_returnsUpdatedData() {
        AccountDto updated = testAccount();
        updated.setFirstName("Updated");
        updated.setLastName("Name");

        AccountDto response = restTestClient
                .put()
                .uri("/account/" + createdId)
                .header("Authorization", "Bearer " + adminToken)
                .body(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Updated");
        assertThat(response.getLastName()).isEqualTo("Name");
    }

    // ── patchUpdate ───────────────────────────────────────────────────────────

    @Test
    void patchUpdate_singleField_returnsUpdatedField() {
        AccountDto response = restTestClient
                .patch()
                .uri("/account/" + createdId)
                .header("Authorization", "Bearer " + adminToken)
                .body(Map.of("firstName", "Patched"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Patched");
        assertThat(response.getLastName()).isEqualTo("Test");
    }

    // ── updatePaidUntil ───────────────────────────────────────────────────────

    @Test
    void updatePaidUntil_validDate_setsPaidUntilField() {
        String newDate = "2030-12-31T00:00:00";

        AccountDto response = restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/account/" + createdId + "/paid-until")
                        .queryParam("newDate", newDate)
                        .build())
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getPaidUntil()).isNotNull();
        assertThat(response.getPaidUntil()).isEqualTo(LocalDateTime.parse(newDate, DateTimeFormatter.ofPattern(CommonUnits.DATE_FORMAT)));
    }

    @Test
    void updatePaidUntil_invalidDateFormat_returnsBadRequest() {
        restTestClient
                .patch()
                .uri("/account/" + createdId + "/paid-until?newDate=not-a-date")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existingAccount_returnsNoContent() {
        restTestClient
                .delete()
                .uri("/account/" + createdId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNoContent();

        createdId = null; // already deleted — skip tearDown cleanup
    }

    @Test
    void deleteById_nonExistentId_returnsNotFound() {
        restTestClient
                .delete()
                .uri("/account/999999999999")
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private AccountDto testAccount() {
        AccountDto dto = new AccountDto();
        dto.setFirstName("Integration");
        dto.setLastName("Test");
        dto.setCardNumber("TEST-CARD-001");
        dto.setIsBlocked(false);
        return dto;
    }
}
