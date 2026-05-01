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

    @Operation(
            summary = "Get accounts page",
            description = "Returns a paginated list of accounts. " +
                    "Pass `query` to filter by first name, patronymic, last name, or card number (case-insensitive). " +
                    "Supports sorting by any AccountDto field. " +
                    "Multiple `sort` parameters are allowed (e.g. `sort=lastName,asc&sort=firstName,asc`)."
    )
    @Parameters({
            @Parameter(name = "query", in = ParameterIn.QUERY,
                    description = "Optional search string matched against name fields and card number",
                    schema = @Schema(type = "string")),
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number, 0-based",
                    schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Number of items per page",
                    schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(name = "sort", in = ParameterIn.QUERY,
                    description = "Sort field and direction: `field,asc` or `field,desc` (e.g. `lastName,asc`)",
                    schema = @Schema(type = "string"), example = "lastName,asc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<Page<AccountDto>> findPage(
            @Parameter(hidden = true) String query,
            @Parameter(hidden = true) Pageable pageable
    );

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
