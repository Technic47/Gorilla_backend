package ru.gorilla.gim.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gorilla.gim.backend.controller.api.AccountControllerApi;
import ru.gorilla.gim.backend.dto.AccountDto;
import ru.gorilla.gim.backend.service.AccountService;

import ru.gorilla.gim.backend.util.CommonUnits;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController implements AccountControllerApi {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> findById(@PathVariable Long id) {
        AccountDto dto = accountService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<AccountDto> add(@RequestBody AccountDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.add(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> update(@PathVariable Long id, @RequestBody AccountDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(accountService.update(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountDto> patchUpdate(@PathVariable Long id,
                                                   @RequestBody Map<String, Object> fields) {
        AccountDto dto = accountService.patchUpdate(id, fields);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/paid-until")
    public ResponseEntity<AccountDto> updatePaidUntil(@PathVariable Long id,
                                                       @RequestParam String newDate) {
        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDateTime.parse(newDate, DateTimeFormatter.ofPattern(CommonUnits.DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Cannot parse date: " + newDate + ". Expected format: " + CommonUnits.DATE_FORMAT);
        }
        AccountDto dto = accountService.patchUpdate(id, Map.of("paidUntil", parsedDate));
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        accountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
