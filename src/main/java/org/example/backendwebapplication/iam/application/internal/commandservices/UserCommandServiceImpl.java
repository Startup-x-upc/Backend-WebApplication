package org.example.backendwebapplication.iam.application.internal.commandservices;

import org.example.backendwebapplication.iam.application.commandservices.UserCommandService;
import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.commands.LoginCommand;
import org.example.backendwebapplication.iam.domain.model.commands.RegisterDriverCommand;
import org.example.backendwebapplication.iam.domain.model.commands.RegisterPassengerCommand;
import org.example.backendwebapplication.iam.domain.model.commands.UpdateProfileCommand;
import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.iam.domain.model.valueobjects.*;
import org.example.backendwebapplication.iam.domain.repositories.ProfileRepository;
import org.example.backendwebapplication.iam.domain.repositories.UserRepository;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link UserCommandService}.
 * <p>Handles all write use cases for the IAM bounded context.</p>
 */
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    // ── Register Passenger ──────────────────────────────────────────

    /**
     * Registers a new PASSENGER user.
     * <p>Creates User + Profile in the same transaction.
     * Emits {@code UserRegisteredEvent} for audit/future consumers.</p>
     */
    @Override
    @Transactional
    public Result<User, ApplicationError> handle(RegisterPassengerCommand command) {
        var email = new Email(command.email());

        if (userRepository.existsByEmail(email)) {
            return Result.failure(ApplicationError.conflict(
                    "EMAIL", "Email already registered: " + command.email()));
        }

        var passwordHash = new PasswordHash(command.password());
        var fullName = new FullName(command.fullName());

        var user = new User(email, passwordHash, UserRole.PASSENGER);
        user.onPassengerRegistered(command.fullName());

        var savedUser = userRepository.save(user);

        var profile = new Profile(savedUser.getUserId(), fullName, null);
        profileRepository.save(profile);

        return Result.success(savedUser);
    }

    // ── Register Driver ─────────────────────────────────────────────

    /**
     * Registers a new DRIVER user.
     * <p>Creates User + Profile in the same transaction.
     * Emits {@code DriverRegisteredEvent} so Driver Management and
     * Monetization can create their own entities.</p>
     */
    @Override
    @Transactional
    public Result<User, ApplicationError> handle(RegisterDriverCommand command) {
        var email = new Email(command.email());

        if (userRepository.existsByEmail(email)) {
            return Result.failure(ApplicationError.conflict(
                    "EMAIL", "Email already registered: " + command.email()));
        }

        var passwordHash = new PasswordHash(command.password());
        var fullName = new FullName(command.fullName());

        var user = new User(email, passwordHash, UserRole.DRIVER);
        user.onDriverRegistered(
                command.fullName(), command.vehicleType(),
                command.licenseNumber(), command.soatNumber());

        var savedUser = userRepository.save(user);

        var profile = new Profile(savedUser.getUserId(), fullName, null);
        profileRepository.save(profile);

        return Result.success(savedUser);
    }

    // ── Login ───────────────────────────────────────────────────────

    /**
     * Authenticates user.
     * <p>Returns VALIDATION_ERROR if email not found or password mismatch.
     * The error message is intentionally ambiguous to prevent
     * user enumeration attacks.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public Result<User, ApplicationError> handle(LoginCommand command) {
        var email = new Email(command.email());
        var user = userRepository.findByEmail(email);

        if (user.isEmpty() || !user.get().passwordMatches(command.password())) {
            return Result.failure(ApplicationError.validationError(
                    "credentials", "Invalid email or password"));
        }

        return Result.success(user.get());
    }

    // ── Update Profile ──────────────────────────────────────────────

    /**
     * Updates an existing profile.
     * <p>Authorization: only the profile owner or an ADMIN can update.
     * Emits {@code ProfileUpdatedEvent} for cross-context sync.</p>
     */
    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(UpdateProfileCommand command) {
        var profile = profileRepository.findById(command.profileId());

        if (profile.isEmpty()) {
            return Result.failure(ApplicationError.notFound(
                    "PROFILE", command.profileId().toString()));
        }

        // Authorisation: owner or ADMIN
        var requester = userRepository.findById(command.requesterUserId());
        if (requester.isEmpty()) {
            return Result.failure(ApplicationError.notFound(
                    "USER", command.requesterUserId().toString()));
        }

        boolean isOwner = profile.get().getUserId().equals(command.requesterUserId());
        boolean isAdmin = requester.get().getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "PROFILE_OWNERSHIP",
                    "Only the profile owner or an ADMIN can update this profile"));
        }

        var newFullName = new FullName(command.fullName());
        profile.get().update(newFullName, command.photoUrl());
        var savedProfile = profileRepository.save(profile.get());

        // Register and publish the integration event via the User aggregate root
        var owner = userRepository.findById(savedProfile.getUserId());
        owner.ifPresent(user -> {
            user.onProfileUpdated(
                    savedProfile.getProfileId(),
                    savedProfile.getFullName(),
                    savedProfile.getPhotoUrl());
            userRepository.save(user);
        });

        return Result.success(savedProfile);
    }
}
