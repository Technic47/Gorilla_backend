package ru.gorilla.gim.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gorilla.gim.backend.dto.MetricsBucketDto;
import ru.gorilla.gim.backend.dto.MetricsSummaryDto;
import ru.gorilla.gim.backend.repository.AccountRepository;
import ru.gorilla.gim.backend.repository.PaymentRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MetricsService {

    public enum Metric {REGISTRATIONS, PAYMENTS}

    public enum Granularity {DAY, WEEK, MONTH}

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public MetricsSummaryDto summary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime weekStart = today.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime yearStart = today.withDayOfYear(1).atStartOfDay();

        return MetricsSummaryDto.builder()
                .accountsTotal(accountRepository.count())
                .registrationsWeek(accountRepository.countByCreatedBetween(weekStart, now))
                .registrationsMonth(accountRepository.countByCreatedBetween(monthStart, now))
                .registrationsYear(accountRepository.countByCreatedBetween(yearStart, now))
                .paymentsWeek(paymentRepository.countByCreatedBetween(weekStart, now))
                .paymentsMonth(paymentRepository.countByCreatedBetween(monthStart, now))
                .paymentsYear(paymentRepository.countByCreatedBetween(yearStart, now))
                .generatedAt(now)
                .build();
    }

    public List<MetricsBucketDto> series(Metric metric,
                                         Granularity granularity,
                                         LocalDateTime from,
                                         LocalDateTime to) {
        LocalDateTime rangeTo = to != null ? to : LocalDateTime.now();
        LocalDateTime rangeFrom = from != null ? from : defaultFrom(granularity, rangeTo);

        List<LocalDateTime> timestamps = switch (metric) {
            case REGISTRATIONS -> accountRepository.findCreatedTimestamps(rangeFrom, rangeTo);
            case PAYMENTS -> paymentRepository.findCreatedTimestamps(rangeFrom, rangeTo);
        };

        Function<LocalDateTime, LocalDateTime> bucketOf = bucketFunction(granularity);

        // Pre-fill empty buckets so chart lines stay continuous on quiet days.
        Map<LocalDateTime, Long> counts = new TreeMap<>();
        LocalDateTime cursor = bucketOf.apply(rangeFrom);
        LocalDateTime endBucket = bucketOf.apply(rangeTo);
        while (!cursor.isAfter(endBucket)) {
            counts.put(cursor, 0L);
            cursor = nextBucket(cursor, granularity);
        }
        for (LocalDateTime ts : timestamps) {
            counts.merge(bucketOf.apply(ts), 1L, Long::sum);
        }

        List<MetricsBucketDto> result = new ArrayList<>(counts.size());
        counts.forEach((k, v) -> result.add(new MetricsBucketDto(k, v)));
        return result;
    }

    private Function<LocalDateTime, LocalDateTime> bucketFunction(Granularity g) {
        return switch (g) {
            case DAY -> ts -> ts.toLocalDate().atStartOfDay();
            case WEEK -> ts -> ts.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
            case MONTH -> ts -> ts.toLocalDate().withDayOfMonth(1).atStartOfDay();
        };
    }

    private LocalDateTime defaultFrom(Granularity g, LocalDateTime to) {
        LocalDate toDate = to.toLocalDate();
        return switch (g) {
            case DAY -> toDate.minusDays(29).atStartOfDay();
            case WEEK -> toDate.with(DayOfWeek.MONDAY).minusWeeks(11).atStartOfDay();
            case MONTH -> toDate.withDayOfMonth(1).minusMonths(11).atStartOfDay();
        };
    }

    private LocalDateTime nextBucket(LocalDateTime current, Granularity g) {
        return switch (g) {
            case DAY -> current.plusDays(1);
            case WEEK -> current.plusWeeks(1);
            case MONTH -> current.plusMonths(1);
        };
    }
}
