package ru.gorilla.gim.backend.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.gorilla.gim.backend.entity.UserEntity;
import ru.gorilla.gim.backend.repository.UserRepository;
import ru.gorilla.gim.backend.util.Role;

import java.time.LocalDateTime;

import static ru.gorilla.gim.backend.util.CommonUnits.AVATAR_BUCKET;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppInitializer implements ApplicationRunner {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinioClient minioClient;

    @Override
    public void run(ApplicationArguments args) {
        createDefaultAdminUser();
        createAvatarMinioBucket();
    }

    private void createDefaultAdminUser() {
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

    private void createAvatarMinioBucket() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(AVATAR_BUCKET).build()
            );
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(AVATAR_BUCKET).build());
                log.info("Default bucket created");
            } else {
                log.info("Default bucket already exists");
            }
        } catch (Exception e) {
            log.error("Default bucket check failed!");
            throw new RuntimeException("Default bucket check failed!");
        }

    }
}
