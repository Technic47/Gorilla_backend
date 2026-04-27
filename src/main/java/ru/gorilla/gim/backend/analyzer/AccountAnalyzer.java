package ru.gorilla.gim.backend.analyzer;

import ru.gorilla.gim.backend.entity.AccountEntity;

public interface AccountAnalyzer {

    AnalyzerWarning analyze(AccountEntity account);
}
