package org.example.backendwebapplication.iam.interfaces.rest;

import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backendwebapplication.iam.application.commandservices.UserCommandService;
import org.example.backendwebapplication.iam.application.queryservices.ProfileQueryService;
import org.example.backendwebapplication.iam.application.queryservices.UserQueryService;
import org.example.backendwebapplication.iam.domain.model.commands.LoginCommand;
import org.example.backendwebapplication.iam.domain.model.commands.RegisterDriverCommand;
import org.example.backendwebapplication.iam.domain.model.commands.RegisterPassengerCommand;
import org.example.backendwebapplication.iam.domain.model.queries.GetProfileByUserIdQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetUserByEmailQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetUserByIdQuery;
import org.example.backendwebapplication.iam.infrastructure.security.JwtService;
import org.example.backendwebapplication.iam.interfaces.rest.resources.*;
import org.example.backendwebapplication.iam.interfaces.rest.transform.ProfileResourceAssembler;
import org.example.backendwebapplication.iam.interfaces.rest.transform.UserResourceAssembler;
import org.example.backendwebapplication.shared.interfaces.rest.resources.ErrorResource;
import org.example.backendwebapplication.shared.interfaces.rest.transform.ErrorResponseAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login, and token management")
public class AuthController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final ProfileQueryService profileQueryService;
    private final JwtService jwtService;

    // ── Register Passenger ────────────────────────────────────────────

    @PostMapping("/auth/register/passenger")
    @Operation(summary = "Register a new passenger",
               description = "Creates a PASSENGER user and its associated profile")
    public ResponseEntity<?> registerPassenger(@Valid @RequestBody RegisterPassengerResource resource) {
        var command = new RegisterPassengerCommand(
                resource.email(), resource.password(), resource.fullName());

        var result = userCommandService.handle(command);

        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    result.failure().orElseThrow());
        }

        var user = result.success().orElseThrow();
        var profile = profileQueryService.handle(
                new GetProfileByUserIdQuery(user.getUserId())).orElse(null);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var response = new AuthResponseResource(
                UserResourceAssembler.toResource(user),
                profile != null ? ProfileResourceAssembler.toResource(profile, user) : null,
                accessToken, refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── Register Driver ───────────────────────────────────────────────

    @PostMapping("/auth/register/driver")
    @Operation(summary = "Register a new driver",
               description = "Creates a DRIVER user, profile, and emits DriverRegisteredEvent")
    public ResponseEntity<?> registerDriver(@Valid @RequestBody RegisterDriverResource resource) {
        var command = new RegisterDriverCommand(
                resource.email(), resource.password(), resource.fullName(),
                resource.vehicleType(), resource.licenseNumber(), resource.soatNumber());

        var result = userCommandService.handle(command);

        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    result.failure().orElseThrow());
        }

        var user = result.success().orElseThrow();
        var profile = profileQueryService.handle(
                new GetProfileByUserIdQuery(user.getUserId())).orElse(null);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var response = new AuthResponseResource(
                UserResourceAssembler.toResource(user),
                profile != null ? ProfileResourceAssembler.toResource(profile, user) : null,
                accessToken, refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── Login ─────────────────────────────────────────────────────────

    @PostMapping("/auth/login")
    @Operation(summary = "Authenticate a user",
               description = "Validates credentials and returns tokens")
    public ResponseEntity<?> login(@Valid @RequestBody LoginResource resource) {
        var command = new LoginCommand(resource.email(), resource.password());
        var result = userCommandService.handle(command);

        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    result.failure().orElseThrow());
        }

        var user = result.success().orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var response = new AuthResponseResource(
                UserResourceAssembler.toResource(user), null, accessToken, refreshToken);

        return ResponseEntity.ok(response);
    }

    // ── Refresh Token ─────────────────────────────────────────────────

    @PostMapping("/auth/refresh")
    @Operation(summary = "Refresh an expired access token",
               description = "Rotates the refresh token and issues a new access/refresh pair")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest resource) {
        try {
            var tokenPair = jwtService.refreshAccessToken(resource.refreshToken());
            return ResponseEntity.ok(new TokenRefreshResponse(
                    tokenPair.accessToken(), tokenPair.refreshToken()));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResource("INVALID_REFRESH_TOKEN", e.getMessage()));
        }
    }

    // ── Get Current User ──────────────────────────────────────────────

    @GetMapping("/auth/me")
    @Operation(summary = "Get current authenticated user",
               description = "Returns the user from the JWT access token")
    public ResponseEntity<?> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResource("UNAUTHORIZED", "No valid authentication found"));
        }

        UUID userId = (UUID) auth.getPrincipal();
        var userOpt = userQueryService.handle(new GetUserByIdQuery(userId));

        if (userOpt.isPresent()) {
            return ResponseEntity.ok(UserResourceAssembler.toResource(userOpt.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResource("UNAUTHORIZED", "User not found"));
    }

    // ── Check Email ───────────────────────────────────────────────────

    @GetMapping("/auth/check-email")
    @Operation(summary = "Check if an email is already registered")
    public ResponseEntity<CheckEmailResponse> checkEmail(@RequestParam String email) {
        var exists = userQueryService.handle(new GetUserByEmailQuery(email)).isPresent();
        return ResponseEntity.ok(new CheckEmailResponse(exists));
    }
}
