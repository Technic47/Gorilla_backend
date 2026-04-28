package ru.gorilla.gim.backend.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import ru.gorilla.gim.backend.dto.AuthResponse;
import ru.gorilla.gim.backend.dto.ChangeCredentialsRequest;
import ru.gorilla.gim.backend.entity.UserEntity;

@Tag(name = "User", description = "Current user management API")
public interface UserControllerApi {

    @Operation(
            summary = "Change credentials",
            description = "Updates the current user's username and/or password. Returns a fresh JWT token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credentials updated, new token returned",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Current password is incorrect", content = @Content),
            @ApiResponse(responseCode = "409", description = "New username is already taken", content = @Content)
    })
    ResponseEntity<AuthResponse> changeCredentials(
            @RequestBody(description = "Current password (required) plus new username and/or new password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangeCredentialsRequest.class)))
            ChangeCredentialsRequest request,
            UserEntity currentUser
    );
}
