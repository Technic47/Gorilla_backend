package ru.gorilla.gim.backend.analyzer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.gorilla.gim.backend.entity.AccountEntity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ru.gorilla.gim.backend.util.AccountWarningLevel.INFO;
import static ru.gorilla.gim.backend.util.AccountWarningLevel.WARNING;

@Component
public class PayDateAnalyzer implements AccountAnalyzer {

    @Value("${analyzer.payment-warning-period}")
    private String paymentWarningPeriod;

    @Override
    public AnalyzerWarning analyze(AccountEntity account) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime paidUntil = account.getPaidUntil();

        if (paidUntil != null) {
            if (!paidUntil.isBefore(now)) {
                if (ChronoUnit.DAYS.between(now, paidUntil) <= Long.parseLong(paymentWarningPeriod)) {
                    return new AnalyzerWarning(INFO, "У пользователя скоро закончится абонемент!");
                } else return null;
            } else {
                return new AnalyzerWarning(WARNING, "У пользователя закончился абонемент!");
            }
        } else {
            return new AnalyzerWarning(INFO, "У пользователя не оплачен абонемент.");
        }
    }
}
