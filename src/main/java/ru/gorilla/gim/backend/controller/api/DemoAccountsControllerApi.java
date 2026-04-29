package ru.gorilla.gim.backend.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Demo", description = "Demo data management API. Requires ADMIN role.")
@SecurityRequirement(name = "bearerAuth")
public interface DemoAccountsControllerApi {

    @Operation(summary = "Generate demo accounts", description = "Creates the specified number of accounts with randomised demo data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts generated; body contains the count",
                    content = @Content(schema = @Schema(type = "integer", example = "50"))),
            @ApiResponse(responseCode = "400", description = "Invalid amount", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<Integer> generateAccounts(
            @Parameter(description = "Number of accounts to generate", required = true, example = "50") int amount
    );

    @Operation(summary = "Delete all demo accounts", description = "Removes all accounts marked as demo=true and their associated payments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted; body contains the count of removed accounts",
                    content = @Content(schema = @Schema(type = "integer", example = "50"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<Integer> deleteAllDemo();
}
