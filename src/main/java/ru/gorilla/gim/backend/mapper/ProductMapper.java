package ru.gorilla.gim.backend.mapper;

import org.mapstruct.Mapper;
import ru.gorilla.gim.backend.dto.ProductDto;
import ru.gorilla.gim.backend.entity.ProductEntity;

@Mapper(componentModel = "spring")
public abstract class ProductMapper implements AbstractMapper<ProductEntity, ProductDto> {
}
