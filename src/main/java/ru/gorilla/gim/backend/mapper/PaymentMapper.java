package ru.gorilla.gim.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gorilla.gim.backend.dto.PaymentDto;
import ru.gorilla.gim.backend.entity.PaymentEntity;
import ru.gorilla.gim.backend.repository.AccountRepository;
import ru.gorilla.gim.backend.repository.ProductRepository;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper implements AbstractMapper<PaymentEntity, PaymentDto> {

    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected ProductRepository productRepository;

    @Override
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "productId", source = "product.id")
    public abstract PaymentDto entityToDto(PaymentEntity entity);

    @Override
    @Mapping(target = "account", expression = "java(accountRepository.findById(dto.getAccountId()).orElse(null))")
    @Mapping(target = "product", expression = "java(dto.getProductId() != null ? productRepository.findById(dto.getProductId()).orElse(null) : null)")
    public abstract PaymentEntity dtoToEntity(PaymentDto dto);
}
