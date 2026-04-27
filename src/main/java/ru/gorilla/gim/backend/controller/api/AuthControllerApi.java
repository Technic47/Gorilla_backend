package ru.gorilla.gim.backend.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import ru.gorilla.gim.backend.dto.AuthResponse;
import ru.gorilla.gim.backend.dto.LoginRequest;

@Tag(name = "Auth", description = "Authentication and JWT token management API")
public interface AuthControllerApi {

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    ResponseEntity<AuthResponse> login(
            @RequestBody(description = "Username and password", required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))) LoginRequest request
    );

    @Operation(
            summary = "Validate token",
            description = "Checks whether the provided Bearer token is valid and not expired",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token is missing, malformed, or expired", content = @Content)
    })
    ResponseEntity<Void> validate(
            @Parameter(description = "Bearer token — `Bearer <token>`", required = true) String authHeader
    );

    @Operation(
            summary = "Refresh token",
            description = "Issues a new JWT token in exchange for a still-valid Bearer token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New token issued",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token is missing, malformed, or expired", content = @Content)
    })
    ResponseEntity<AuthResponse> refresh(
            @Parameter(description = "Bearer token — `Bearer <token>`", required = true) String authHeader
    );
}
