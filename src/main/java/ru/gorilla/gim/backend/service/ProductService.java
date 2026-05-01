package ru.gorilla.gim.backend.service;

import org.springframework.stereotype.Service;
import ru.gorilla.gim.backend.dto.ProductDto;
import ru.gorilla.gim.backend.entity.ProductEntity;
import ru.gorilla.gim.backend.mapper.ProductMapper;
import ru.gorilla.gim.backend.repository.ProductRepository;

@Service
public class ProductService extends AbstractService<ProductEntity, ProductDto, ProductRepository, ProductMapper> {

    protected ProductService(ProductRepository repository, ProductMapper mapper) {
        super(repository, mapper);
    }
}
