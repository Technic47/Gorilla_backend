package ru.gorilla.gim.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gorilla.gim.backend.dto.PaymentDto;
import ru.gorilla.gim.backend.entity.PaymentEntity;
import ru.gorilla.gim.backend.repository.AccountRepository;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper implements AbstractMapper<PaymentEntity, PaymentDto> {

    @Autowired
    protected AccountRepository accountRepository;

    @Override
    @Mapping(target = "accountId", source = "account.id")
    public abstract PaymentDto entityToDto(PaymentEntity entity);

    @Override
    @Mapping(target = "account", expression = "java(accountRepository.findById(dto.getAccountId()).orElse(null))")
    public abstract PaymentEntity dtoToEntity(PaymentDto dto);
}
