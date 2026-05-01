package ru.gorilla.gim.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<AccountDto> searchPage(String query, Pageable pageable) {
        return repository.searchByQuery(query.trim(), pageable).map(entityMapper::entityToDto);
    }

    @Transactional
    public Integer setAvatar(Long accountId, Long avatarId) {
        return repository.setAccountAvatar(accountId, avatarId);
    }

}
