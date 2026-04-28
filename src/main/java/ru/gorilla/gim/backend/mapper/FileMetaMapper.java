package ru.gorilla.gim.backend.mapper;

import org.mapstruct.Mapper;
import ru.gorilla.gim.backend.dto.FileMetaDto;
import ru.gorilla.gim.backend.entity.FileMetaEntity;

@Mapper(componentModel = "spring")
public abstract class FileMetaMapper implements AbstractMapper<FileMetaEntity, FileMetaDto> {
}
