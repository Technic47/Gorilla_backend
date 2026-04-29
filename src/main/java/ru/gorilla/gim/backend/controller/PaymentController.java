package ru.gorilla.gim.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gorilla.gim.backend.controller.api.PaymentControllerApi;
import ru.gorilla.gim.backend.dto.PaymentDto;
import ru.gorilla.gim.backend.service.PaymentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerApi {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> findAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PaymentDto>> findPage(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(paymentService.findPage(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> findById(@PathVariable Long id) {
        PaymentDto dto = paymentService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDto>> findAllByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.findAllByAccountId(userId));
    }

    @PostMapping
    public ResponseEntity<PaymentDto> add(@RequestBody PaymentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.add(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> update(@PathVariable Long id, @RequestBody PaymentDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(paymentService.update(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PaymentDto> patchUpdate(@PathVariable Long id,
                                                   @RequestBody Map<String, Object> fields) {
        PaymentDto dto = paymentService.patchUpdate(id, fields);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        paymentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
