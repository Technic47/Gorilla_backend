package ru.gorilla.gim.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "file_meta")
@EqualsAndHashCode(callSuper = true)
public class FileMetaEntity extends AbstractEntity {

    private String originalName;
    private String objectKey; // Путь в MinIO
    private String contentType;
    private String status; // Например: PENDING, UPLOADED, FAILED

    // Getters and Setters
}
