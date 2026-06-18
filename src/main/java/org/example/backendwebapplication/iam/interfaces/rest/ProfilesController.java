package org.example.backendwebapplication.iam.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backendwebapplication.iam.application.commandservices.UserCommandService;
import org.example.backendwebapplication.iam.application.queryservices.ProfileQueryService;
import org.example.backendwebapplication.iam.application.queryservices.UserQueryService;
import org.example.backendwebapplication.iam.domain.model.commands.UpdateProfileCommand;
import org.example.backendwebapplication.iam.domain.model.queries.GetProfileByUserIdQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetUserByIdQuery;
import org.example.backendwebapplication.iam.interfaces.rest.resources.UpdateProfileResource;
import org.example.backendwebapplication.iam.interfaces.rest.transform.ProfileResourceAssembler;
import org.example.backendwebapplication.shared.interfaces.rest.resources.ErrorResource;
import org.example.backendwebapplication.shared.interfaces.rest.transform.ErrorResponseAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for profile endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Profiles", description = "User profile queries and updates")
public class ProfilesController {

    private final UserCommandService userCommandService;
    private final ProfileQueryService profileQueryService;
    private final UserQueryService userQueryService;

    // ── Get My Profile ────────────────────────────────────────────────

    @GetMapping("/users/me/profile")
    @Operation(summary = "Get the current user's profile",
               description = "Returns a composed read model (Profile + User email/role)")
    public ResponseEntity<?> getMyProfile() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResource("UNAUTHORIZED", "No valid authentication found"));
        }

        UUID userId = (UUID) auth.getPrincipal();

        var profile = profileQueryService.handle(new GetProfileByUserIdQuery(userId));
        var user = userQueryService.handle(new GetUserByIdQuery(userId));

        if (profile.isEmpty() || user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResource("PROFILE_NOT_FOUND",
                            "Profile not found for the current user"));
        }

        var resource = ProfileResourceAssembler.toMyProfileResource(profile.get(), user.get());
        return ResponseEntity.ok(resource);
    }

    // ── Update Profile ────────────────────────────────────────────────

    @PutMapping("/profiles/{profileId}")
    @Operation(summary = "Update a profile",
               description = "Updates the profile. Only the owner or ADMIN can update.")
    public ResponseEntity<?> updateProfile(
            @PathVariable UUID profileId,
            @Valid @RequestBody UpdateProfileResource resource) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResource("UNAUTHORIZED", "No valid authentication found"));
        }

        UUID requesterUserId = (UUID) auth.getPrincipal();

        var command = new UpdateProfileCommand(
                profileId, requesterUserId,
                resource.fullName(), resource.photoUrl());

        var result = userCommandService.handle(command);

        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(
                    result.failure().orElseThrow());
        }

        var profile = result.success().orElseThrow();
        return ResponseEntity.ok(ProfileResourceAssembler.toResource(profile));
    }
}
