package ru.gorilla.gim.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.gorilla.gim.backend.controller.api.UserControllerApi;
import ru.gorilla.gim.backend.dto.AuthResponse;
import ru.gorilla.gim.backend.dto.ChangeCredentialsRequest;
import ru.gorilla.gim.backend.entity.UserEntity;
import ru.gorilla.gim.backend.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final UserService userService;

    @PatchMapping("/me/credentials")
    public ResponseEntity<AuthResponse> changeCredentials(
            @RequestBody ChangeCredentialsRequest request,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        return ResponseEntity.ok(userService.changeCredentials(currentUser, request));
    }
}
