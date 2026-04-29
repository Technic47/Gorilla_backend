package ru.gorilla.gim.backend.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.gorilla.gim.backend.dto.PaymentDto;

import java.util.List;
import java.util.Map;

@Tag(name = "Payment", description = "Payment management API")
@SecurityRequirement(name = "bearerAuth")
public interface PaymentControllerApi {

    @Operation(summary = "Get all payments", description = "Returns a list of all payments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<List<PaymentDto>> findAll();

    @Operation(summary = "Get payments page", description = "Returns a paginated list of payments. Supports sorting by any PaymentDto field.")
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number, 0-based",
                    schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Number of items per page",
                    schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(name = "sort", in = ParameterIn.QUERY,
                    description = "Sort field and direction: `field,asc` or `field,desc`",
                    schema = @Schema(type = "string"), example = "created,desc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<Page<PaymentDto>> findPage(
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "Get payment by ID", description = "Returns a single payment by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    ResponseEntity<PaymentDto> findById(
            @Parameter(description = "Payment ID", required = true) Long id
    );

    @Operation(summary = "Get payments by user ID", description = "Returns all payments belonging to the specified user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<List<PaymentDto>> findAllByUserId(
            @Parameter(description = "User ID", required = true) Long userId
    );

    @Operation(summary = "Create payment", description = "Creates a new payment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<PaymentDto> add(
            @RequestBody(description = "Payment data", required = true,
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))) PaymentDto dto
    );

    @Operation(summary = "Replace payment", description = "Fully replaces an existing payment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<PaymentDto> update(
            @Parameter(description = "Payment ID", required = true) Long id,
            @RequestBody(description = "Full payment data", required = true,
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))) PaymentDto dto
    );

    @Operation(summary = "Partially update payment", description = "Updates only the provided fields of a payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment patched",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patch data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    ResponseEntity<PaymentDto> patchUpdate(
            @Parameter(description = "Payment ID", required = true) Long id,
            @RequestBody(description = "Map of fields to update", required = true,
                    content = @Content(schema = @Schema(type = "object"))) Map<String, Object> fields
    );

    @Operation(summary = "Delete payment", description = "Deletes a payment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    ResponseEntity<Void> deleteById(
            @Parameter(description = "Payment ID", required = true) Long id
    );
}
