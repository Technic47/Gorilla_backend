package ru.gorilla.gim.backend.config.initializer;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
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
import static ru.gorilla.gim.backend.util.CommonUnits.DB_BACKUP_BUCKET;

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
        createDbBackupBucket();
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
                log.info("Avatars bucket created");
            } else {
                log.info("Avatars bucket already exists");
            }
            String policy = String.format("""
                    {
                      "Version": "2012-10-17",
                      "Statement": [{
                        "Effect": "Allow",
                        "Principal": {"AWS": ["*"]},
                        "Action": ["s3:GetObject"],
                        "Resource": ["arn:aws:s3:::%s/*"]
                      }]
                    }
                    """, AVATAR_BUCKET);
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(AVATAR_BUCKET)
                            .config(policy)
                            .build()
            );
            log.info("Bucket public-read policy applied");
        } catch (Exception e) {
            log.error("Default bucket check failed!");
            throw new RuntimeException("Default bucket check failed!");
        }
    }

    private void createDbBackupBucket() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(DB_BACKUP_BUCKET).build()
            );
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(DB_BACKUP_BUCKET).build());
                log.info("DB backup bucket created");
            } else {
                log.info("DB backup bucket already exists");
            }
        } catch (Exception e) {
            log.error("DB backup bucket check failed!");
            throw new RuntimeException("DB backup bucket check failed!");
        }
    }
}
