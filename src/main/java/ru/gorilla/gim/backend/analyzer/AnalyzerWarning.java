package ru.gorilla.gim.backend.analyzer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.gorilla.gim.backend.util.AccountWarningLevel;

@Getter
@Setter
@AllArgsConstructor
public class AnalyzerWarning {
    private AccountWarningLevel level;
    private String message;
}
