package ru.gorilla.gim.backend.analyzer;

import org.springframework.stereotype.Component;
import ru.gorilla.gim.backend.entity.AccountEntity;

import static ru.gorilla.gim.backend.util.AccountWarningLevel.CRITICAL_WARNING;

@Component
public class BlockedUserAnalyzer implements AccountAnalyzer {
    @Override
    public AnalyzerWarning analyze(AccountEntity account) {
        return account.getIsBlocked() ? new AnalyzerWarning(CRITICAL_WARNING, "Пользователь заблокирован") : null;
    }
}
