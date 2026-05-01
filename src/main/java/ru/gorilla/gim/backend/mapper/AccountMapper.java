package ru.gorilla.gim.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gorilla.gim.backend.analyzer.AccountAnalyzer;
import ru.gorilla.gim.backend.analyzer.AnalyzerWarning;
import ru.gorilla.gim.backend.dto.AccountDto;
import ru.gorilla.gim.backend.entity.AccountEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public abstract class AccountMapper implements AbstractMapper<AccountEntity, AccountDto> {

    @Autowired
    protected List<AccountAnalyzer> analyzers;

    @Override
    @Mapping(target = "warnings", source = "entity", qualifiedByName = "analyzeAccount")
//    @Mapping(target = "paidUntil", source = "entity.id", qualifiedByName = "getPaidUntilDate")
    public abstract AccountDto entityToDto(AccountEntity entity);

    @Named("analyzeAccount")
    protected Collection<AnalyzerWarning> analyzeAccount(AccountEntity entity) {
        return analyzers.stream()
                .map(analyzer -> analyzer.analyze(entity))
                .filter(Objects::nonNull)
                .toList();
    }
}