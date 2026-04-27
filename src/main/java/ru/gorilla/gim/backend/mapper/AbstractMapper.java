package ru.gorilla.gim.backend.mapper;

import ru.gorilla.gim.backend.dto.AbstractDto;
import ru.gorilla.gim.backend.entity.AbstractEntity;

import java.util.List;

public interface AbstractMapper<E extends AbstractEntity, D extends AbstractDto> {
    D entityToDto(E entity);

    E dtoToEntity(D dto);

    List<D> allEntitiesToDtos(List<E> entities);

    List<E> allDtosToEntities(List<D> entities);
}
