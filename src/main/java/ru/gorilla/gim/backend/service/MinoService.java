package ru.gorilla.gim.backend.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static ru.gorilla.gim.backend.util.CommonUnits.AVATAR_BUCKET;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinoService {

    private final static int URL_EXPIRY_MINUTES = 10;

    private final MinioClient minioClient;

    public String getPreSignedUploadUrlForAvatar(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.PUT)
                            .bucket(AVATAR_BUCKET)
                            .object(fileName)
                            .expiry(URL_EXPIRY_MINUTES, TimeUnit.MINUTES) // URL expires in 15 mins
                            .build());
        } catch (Exception ex) {
            log.error("Error getting presigned object url for file {}", fileName, ex);
            throw new RuntimeException("Error getting presigned object url for file " + fileName, ex);
        }
    }
}
