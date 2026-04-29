package ru.gorilla.gim.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gorilla.gim.backend.controller.api.DemoAccountsControllerApi;
import ru.gorilla.gim.backend.service.DemoAccountsService;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DemoAccountsController implements DemoAccountsControllerApi {

    private final DemoAccountsService demoAccountsService;

    @PostMapping("/accounts")
    public ResponseEntity<Integer> generateAccounts(@RequestParam int amount) {
        return ResponseEntity.ok(demoAccountsService.generateAccounts(amount));
    }

    @DeleteMapping("/accounts")
    public ResponseEntity<Integer> deleteAllDemo() {
        return ResponseEntity.ok(demoAccountsService.deleteAllDemo());
    }
}
