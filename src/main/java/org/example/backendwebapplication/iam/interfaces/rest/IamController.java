package org.example.backendwebapplication.iam.interfaces.rest;

import org.example.backendwebapplication.iam.application.commandservices.IamCommandService;
import org.example.backendwebapplication.iam.application.queryservices.IamQueryService;
import org.example.backendwebapplication.iam.domain.model.commands.SignInCommand;
import org.example.backendwebapplication.iam.domain.model.commands.UpdateUserProfileCommand;
import org.example.backendwebapplication.iam.domain.model.queries.GetAllProfilesQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetProfileByAccountIdQuery;
import org.example.backendwebapplication.iam.interfaces.rest.resources.*;
import org.example.backendwebapplication.iam.interfaces.rest.transform.*;
import org.example.backendwebapplication.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST controller for the IAM bounded context.
 *
 * <p>Exposes endpoints for account registration, authentication,
 * and user profile management.</p>
 */
@RestController
@RequestMapping("/api/v1/iam")
@RequiredArgsConstructor
@Tag(name = "IAM", description = "Authentication and user profile management")
@CrossOrigin(origins = "*")
public class IamController {

    private final IamCommandService commandService;
    private final IamQueryService   queryService;

    /**
     * Registers a new account (PASSENGER or DRIVER).
     * Auto-creates the associated user profile.
     *
     * @param resource the sign-up request body
     * @return 201 Created with account info, or 409 Conflict if email is taken
     */
    @PostMapping("/sign-up")
    @Operation(summary = "Register a new account", description = "Creates a new PASSENGER or DRIVER account and its associated profile")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpResource resource) {
        var command = CreateAccountCommandFromResourceAssembler.toCommandFromResource(resource);
        var result  = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AccountResourceAssembler::toResourceFromDomain,
                HttpStatus.CREATED);
    }

    /**
     * Authenticates an existing account.
     *
     * @param resource the sign-in request body
     * @return 200 OK with account info, or 400 if credentials are invalid
     */
    @PostMapping("/sign-in")
    @Operation(summary = "Authenticate an account", description = "Validates credentials and returns account information")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource resource) {
        var command = new SignInCommand(resource.email(), resource.password());
        var result  = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                AccountResourceAssembler::toResourceFromDomain,
                HttpStatus.OK);
    }

    /**
     * Retrieves all user profiles.
     *
     * @return 200 OK with list of profiles
     */
    @GetMapping("/profiles")
    @Operation(summary = "Get all user profiles")
    public ResponseEntity<?> getAllProfiles() {
        var profiles = queryService.handle(new GetAllProfilesQuery()).stream()
                .map(UserProfileResourceAssembler::toResourceFromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(profiles);
    }

    /**
     * Retrieves the profile associated with a specific account.
     *
     * @param accountId the account identifier
     * @return 200 OK with the profile, or 404 Not Found
     */
    @GetMapping("/profiles/account/{accountId}")
    @Operation(summary = "Get profile by account ID")
    public ResponseEntity<?> getProfileByAccountId(@PathVariable Long accountId) {
        var result = queryService.handle(new GetProfileByAccountIdQuery(accountId));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserProfileResourceAssembler::toResourceFromDomain,
                HttpStatus.OK);
    }

    /**
     * Updates the profile associated with a specific account.
     *
     * @param accountId the account identifier
     * @param resource  the update request body
     * @return 200 OK with updated profile, or 404 Not Found
     */
    @PutMapping("/profiles/account/{accountId}")
    @Operation(summary = "Update profile by account ID")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateUserProfileResource resource) {
        var command = new UpdateUserProfileCommand(accountId, resource.fullName(), resource.photoUrl());
        var result  = commandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                UserProfileResourceAssembler::toResourceFromDomain,
                HttpStatus.OK);
    }
}
