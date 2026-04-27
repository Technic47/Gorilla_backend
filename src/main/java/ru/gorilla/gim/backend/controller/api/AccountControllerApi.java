package ru.gorilla.gim.backend.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import ru.gorilla.gim.backend.dto.AccountDto;

import java.util.List;
import java.util.Map;

@Tag(name = "Account", description = "Account management API")
@SecurityRequirement(name = "bearerAuth")
public interface AccountControllerApi {

    @Operation(summary = "Get all accounts", description = "Returns a list of all accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<List<AccountDto>> findAll();

    @Operation(summary = "Get account by ID", description = "Returns a single account by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    ResponseEntity<AccountDto> findById(
            @Parameter(description = "Account ID", required = true) Long id
    );

    @Operation(summary = "Create account", description = "Creates a new account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "409", description = "Account already exists (constraint violation)", content = @Content)
    })
    ResponseEntity<AccountDto> add(
            @RequestBody(description = "Account data", required = true,
                    content = @Content(schema = @Schema(implementation = AccountDto.class))) AccountDto dto
    );

    @Operation(summary = "Replace account", description = "Fully replaces an existing account by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account updated",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "409", description = "Constraint violation", content = @Content)
    })
    ResponseEntity<AccountDto> update(
            @Parameter(description = "Account ID", required = true) Long id,
            @RequestBody(description = "Full account data", required = true,
                    content = @Content(schema = @Schema(implementation = AccountDto.class))) AccountDto dto
    );

    @Operation(summary = "Partially update account", description = "Updates only the provided fields of an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account patched",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patch data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    ResponseEntity<AccountDto> patchUpdate(
            @Parameter(description = "Account ID", required = true) Long id,
            @RequestBody(description = "Map of fields to update", required = true,
                    content = @Content(schema = @Schema(type = "object"))) Map<String, Object> fields
    );

    @Operation(
            summary = "Update paidUntil date",
            description = "Sets the paidUntil field for an account. Date must follow the format `" + "yyyy-MM-dd'T'HH:mm:ss" + "` (e.g. `2025-12-31T00:00:00`)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "paidUntil updated",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Date string could not be parsed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    ResponseEntity<AccountDto> updatePaidUntil(
            @Parameter(description = "Account ID", required = true) Long id,
            @Parameter(description = "New paidUntil value in yyyy-MM-dd'T'HH:mm:ss format", required = true,
                    example = "2025-12-31T00:00:00") String newDate
    );

    @Operation(summary = "Delete account", description = "Deletes an account by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    ResponseEntity<Void> deleteById(
            @Parameter(description = "Account ID", required = true) Long id
    );
}
