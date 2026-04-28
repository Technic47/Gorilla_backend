package ru.gorilla.gim.backend.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Database Backup", description = "Database backup and restore management API. Requires ADMIN role.")
@SecurityRequirement(name = "bearerAuth")
public interface DatabaseBackupControllerApi {

    @Operation(summary = "Trigger backup", description = "Starts a database backup asynchronously. Returns immediately.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Backup started", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<Void> backup();

    @Operation(summary = "Restore database", description = "Restores the database from the specified backup file stored in MinIO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restore completed successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Restore failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<Void> restore(
            @Parameter(description = "Backup object name in MinIO (e.g. dump-2026-04-28T03:00:00.sql)", required = true)
            String objectName
    );
}
