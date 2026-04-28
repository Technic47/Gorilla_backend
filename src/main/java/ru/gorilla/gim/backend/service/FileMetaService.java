package ru.gorilla.gim.backend.service;

import org.springframework.stereotype.Service;
import ru.gorilla.gim.backend.dto.FileMetaDto;
import ru.gorilla.gim.backend.entity.FileMetaEntity;
import ru.gorilla.gim.backend.mapper.FileMetaMapper;
import ru.gorilla.gim.backend.repository.FileMetadataRepository;

@Service
public class FileMetaService extends AbstractService <
        FileMetaEntity, FileMetaDto, FileMetadataRepository, FileMetaMapper>{

    protected FileMetaService(FileMetadataRepository repository, FileMetaMapper mapper) {
        super(repository, mapper);
    }
}
