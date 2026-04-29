package ru.gorilla.gim.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackupFileDto {
    private String objectName;
    private long size;
    private LocalDateTime createdAt;
}
