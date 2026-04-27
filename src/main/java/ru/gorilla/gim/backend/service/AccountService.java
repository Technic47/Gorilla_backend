package ru.gorilla.gim.backend.service;

import org.springframework.stereotype.Service;
import ru.gorilla.gim.backend.dto.AccountDto;
import ru.gorilla.gim.backend.entity.AccountEntity;
import ru.gorilla.gim.backend.mapper.AccountMapper;
import ru.gorilla.gim.backend.repository.AccountRepository;

@Service
public class AccountService extends AbstractService<
        AccountEntity, AccountDto, AccountRepository, AccountMapper> {

    protected AccountService(AccountRepository repository, AccountMapper mapper) {
        super(repository, mapper);
    }
}
