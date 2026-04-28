package ru.gorilla.gim.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.gorilla.gim.backend.analyzer.AnalyzerWarning;

import java.time.LocalDateTime;
import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto extends AbstractDto {
    private String firstName;
    private String secondName;
    private String lastName;
    private String cardNumber;
    private Boolean isBlocked = false;
    private LocalDateTime paidUntil;
    private FileMetaDto avatar;
    private Collection<AnalyzerWarning> warnings;
}
