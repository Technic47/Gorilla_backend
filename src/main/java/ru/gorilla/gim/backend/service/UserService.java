package ru.gorilla.gim.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.gorilla.gim.backend.config.JWT.JwtService;
import ru.gorilla.gim.backend.dto.AuthResponse;
import ru.gorilla.gim.backend.dto.ChangeCredentialsRequest;
import ru.gorilla.gim.backend.entity.UserEntity;
import ru.gorilla.gim.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse changeCredentials(UserEntity currentUser, ChangeCredentialsRequest request) {
        if (!passwordEncoder.matches(request.currentPassword(), currentUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }

        if (request.newUsername() != null && !request.newUsername().equals(currentUser.getUsername())) {
            if (userRepository.findByUsername(request.newUsername()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
            }
            currentUser.setUsername(request.newUsername());
        }

        if (request.newPassword() != null) {
            currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
        }

        userRepository.save(currentUser);
        return new AuthResponse(jwtService.generateToken(currentUser));
    }
}
