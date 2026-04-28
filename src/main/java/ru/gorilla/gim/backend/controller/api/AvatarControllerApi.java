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
import ru.gorilla.gim.backend.dto.FileMetaDto;
import ru.gorilla.gim.backend.dto.FileRegistrationRequest;
import ru.gorilla.gim.backend.dto.UploadRegistrationResponse;

import java.util.List;
import java.util.Map;

@Tag(name = "Avatar", description = "Avatar file metadata management API")
@SecurityRequirement(name = "bearerAuth")
public interface AvatarControllerApi {

    @Operation(summary = "Register avatar upload", description = "Generates a presigned MinIO URL for uploading an avatar and saves file metadata")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Upload URL generated",
                    content = @Content(schema = @Schema(implementation = UploadRegistrationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error generating presigned URL", content = @Content)
    })
    ResponseEntity<UploadRegistrationResponse> registerUpload(
            @RequestBody(description = "File registration request", required = true,
                    content = @Content(schema = @Schema(implementation = FileRegistrationRequest.class))) FileRegistrationRequest request
    );

    @Operation(summary = "Get all file metadata records", description = "Returns a list of all avatar file metadata")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Records retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FileMetaDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<List<FileMetaDto>> findAll();

    @Operation(
            summary = "Get file metadata page",
            description = "Returns a paginated list of avatar file metadata records. " +
                    "Supports sorting by any FileMetaDto field."
    )
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number, 0-based",
                    schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Number of items per page",
                    schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(name = "sort", in = ParameterIn.QUERY,
                    description = "Sort field and direction: `field,asc` or `field,desc`",
                    schema = @Schema(type = "string"), example = "originalName,asc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page retrieved",
                    content = @Content(schema = @Schema(implementation = FileMetaDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<Page<FileMetaDto>> findPage(
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "Get file metadata by ID", description = "Returns a single avatar file metadata record by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found",
                    content = @Content(schema = @Schema(implementation = FileMetaDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Record not found", content = @Content)
    })
    ResponseEntity<FileMetaDto> findById(
            @Parameter(description = "File metadata ID", required = true) Long id
    );

    @Operation(summary = "Replace file metadata", description = "Fully replaces an existing file metadata record by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record updated",
                    content = @Content(schema = @Schema(implementation = FileMetaDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Record not found", content = @Content)
    })
    ResponseEntity<FileMetaDto> update(
            @Parameter(description = "File metadata ID", required = true) Long id,
            @RequestBody(description = "Full file metadata", required = true,
                    content = @Content(schema = @Schema(implementation = FileMetaDto.class))) FileMetaDto dto
    );

    @Operation(summary = "Partially update file metadata", description = "Updates only the provided fields of a file metadata record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record patched",
                    content = @Content(schema = @Schema(implementation = FileMetaDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patch data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Record not found", content = @Content)
    })
    ResponseEntity<FileMetaDto> patchUpdate(
            @Parameter(description = "File metadata ID", required = true) Long id,
            @RequestBody(description = "Map of fields to update", required = true,
                    content = @Content(schema = @Schema(type = "object"))) Map<String, Object> fields
    );

    @Operation(summary = "Delete file metadata", description = "Deletes a file metadata record by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Record deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Record not found", content = @Content)
    })
    ResponseEntity<Void> deleteById(
            @Parameter(description = "File metadata ID", required = true) Long id
    );
}
