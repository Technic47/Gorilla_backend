package ru.gorilla.gim.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricsSummaryDto {
    private long accountsTotal;
    private long registrationsWeek;
    private long registrationsMonth;
    private long registrationsYear;
    private long paymentsWeek;
    private long paymentsMonth;
    private long paymentsYear;
    private LocalDateTime generatedAt;
}
