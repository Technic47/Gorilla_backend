package ru.gorilla.gim.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.gorilla.gim.backend.entity.UserEntity;
import ru.gorilla.gim.backend.repository.UserRepository;
import ru.gorilla.gim.backend.util.Role;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }
        UserEntity admin = new UserEntity();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setCreated(LocalDateTime.now());
        admin.setUpdated(LocalDateTime.now());
        userRepository.save(admin);
    }
}
