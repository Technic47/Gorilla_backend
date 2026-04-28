package ru.gorilla.gim.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileMetaDto extends AbstractDto {

    private String originalName;
    private String objectKey;
    private String contentType;
    private String status;
}
