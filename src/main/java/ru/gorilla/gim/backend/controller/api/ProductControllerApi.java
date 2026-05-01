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
import ru.gorilla.gim.backend.dto.ProductDto;

import java.util.List;
import java.util.Map;

@Tag(name = "Product", description = "Subscription product management API")
@SecurityRequirement(name = "bearerAuth")
public interface ProductControllerApi {

    @Operation(summary = "Get all products", description = "Returns a list of all subscription products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<List<ProductDto>> findAll();

    @Operation(summary = "Get products page", description = "Returns a paginated list of products.")
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number, 0-based",
                    schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Number of items per page",
                    schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(name = "sort", in = ParameterIn.QUERY,
                    description = "Sort field and direction: `field,asc` or `field,desc`",
                    schema = @Schema(type = "string"), example = "description,asc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    ResponseEntity<Page<ProductDto>> findPage(
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "Get product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<ProductDto> findById(
            @Parameter(description = "Product ID", required = true) Long id
    );

    @Operation(summary = "Create product")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "409", description = "Description must be unique", content = @Content)
    })
    ResponseEntity<ProductDto> add(
            @RequestBody(description = "Product data", required = true,
                    content = @Content(schema = @Schema(implementation = ProductDto.class))) ProductDto dto
    );

    @Operation(summary = "Replace product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "409", description = "Description must be unique", content = @Content)
    })
    ResponseEntity<ProductDto> update(
            @Parameter(description = "Product ID", required = true) Long id,
            @RequestBody(description = "Full product data", required = true,
                    content = @Content(schema = @Schema(implementation = ProductDto.class))) ProductDto dto
    );

    @Operation(summary = "Partially update product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product patched",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patch data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<ProductDto> patchUpdate(
            @Parameter(description = "Product ID", required = true) Long id,
            @RequestBody(description = "Map of fields to update", required = true,
                    content = @Content(schema = @Schema(type = "object"))) Map<String, Object> fields
    );

    @Operation(summary = "Delete product")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    ResponseEntity<Void> deleteById(
            @Parameter(description = "Product ID", required = true) Long id
    );
}
