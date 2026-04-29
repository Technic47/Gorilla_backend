package ru.gorilla.gim.backend.service;

import org.springframework.stereotype.Service;
import ru.gorilla.gim.backend.dto.PaymentDto;
import ru.gorilla.gim.backend.entity.PaymentEntity;
import ru.gorilla.gim.backend.mapper.PaymentMapper;
import ru.gorilla.gim.backend.repository.PaymentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService extends AbstractService<
        PaymentEntity, PaymentDto, PaymentRepository, PaymentMapper> {

    protected PaymentService(PaymentRepository repository, PaymentMapper mapper) {
        super(repository, mapper);
    }

    public List<PaymentDto> findAllByAccountId(Long accountId) {
        return repository.findAllByAccount_Id(accountId)
                .stream()
                .map(entityMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public PaymentDto findLastByAccountId(Long accountId) {
        PaymentEntity entity = repository.findTopByAccount_IdOrderByCreatedDesc(accountId);
        return entity != null ? entityMapper.entityToDto(entity) : null;
    }
}
