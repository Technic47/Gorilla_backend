package ru.gorilla.gim.backend.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import ru.gorilla.gim.backend.dto.MetricsBucketDto;
import ru.gorilla.gim.backend.dto.MetricsSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Metrics", description = "Aggregated analytics for the admin panel. Requires ADMIN role. " +
        "Note: payment metrics are counts of payment events (subscription extensions), not monetary sums — " +
        "PaymentEntity has no amount field.")
@SecurityRequirement(name = "bearerAuth")
public interface MetricsControllerApi {

    @Operation(summary = "Summary metrics",
            description = "Returns total accounts plus registration and payment counts for the current calendar week, month, and year.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary computed",
                    content = @Content(schema = @Schema(implementation = MetricsSummaryDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content)
    })
    ResponseEntity<MetricsSummaryDto> summary();

    @Operation(summary = "Time series metrics",
            description = "Returns a bucketed time series for charting. Empty buckets are filled with 0 so chart lines stay continuous. " +
                    "If `from`/`to` are omitted, defaults are: DAY → last 30 days, WEEK → last 12 weeks, MONTH → last 12 months. " +
                    "Date format: " + "yyyy-MM-dd'T'HH:mm:ss" + ".")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Series computed",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MetricsBucketDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid metric, granularity, or date", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content)
    })
    ResponseEntity<List<MetricsBucketDto>> series(
            @Parameter(description = "Metric to chart", required = true,
                    schema = @Schema(allowableValues = {"registrations", "payments"})) String metric,
            @Parameter(description = "Bucket size",
                    schema = @Schema(allowableValues = {"day", "week", "month"}, defaultValue = "day")) String granularity,
            @Parameter(description = "Range start (inclusive). Format: yyyy-MM-dd'T'HH:mm:ss",
                    example = "2026-01-01T00:00:00") LocalDateTime from,
            @Parameter(description = "Range end (exclusive). Format: yyyy-MM-dd'T'HH:mm:ss",
                    example = "2026-04-30T00:00:00") LocalDateTime to
    );
}
