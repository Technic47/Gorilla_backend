package ru.gorilla.gim.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gorilla.gim.backend.controller.api.MetricsControllerApi;
import ru.gorilla.gim.backend.dto.MetricsBucketDto;
import ru.gorilla.gim.backend.dto.MetricsSummaryDto;
import ru.gorilla.gim.backend.service.MetricsService;
import ru.gorilla.gim.backend.service.MetricsService.Granularity;
import ru.gorilla.gim.backend.service.MetricsService.Metric;
import ru.gorilla.gim.backend.util.CommonUnits;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MetricsController implements MetricsControllerApi {

    private final MetricsService metricsService;

    @GetMapping("/summary")
    public ResponseEntity<MetricsSummaryDto> summary() {
        return ResponseEntity.ok(metricsService.summary());
    }

    @GetMapping("/series")
    public ResponseEntity<List<MetricsBucketDto>> series(
            @RequestParam String metric,
            @RequestParam(defaultValue = "day") String granularity,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = CommonUnits.DATE_FORMAT) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = CommonUnits.DATE_FORMAT) LocalDateTime to) {
        Metric m = parseEnum(Metric.class, metric, "metric");
        Granularity g = parseEnum(Granularity.class, granularity, "granularity");
        return ResponseEntity.ok(metricsService.series(m, g, from, to));
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String raw, String paramName) {
        try {
            return Enum.valueOf(type, raw.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid '" + paramName + "' value: " + raw);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadParam(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid metrics request");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
