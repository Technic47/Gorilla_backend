package ru.gorilla.gim.backend.repository;

import org.springframework.stereotype.Repository;
import ru.gorilla.gim.backend.entity.ProductEntity;

@Repository
public interface ProductRepository extends AbstractRepository<ProductEntity> {
}
