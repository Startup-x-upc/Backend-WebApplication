package org.example.backendwebapplication.iam.application.internal.commandservices;

import org.example.backendwebapplication.iam.application.commandservices.IamCommandService;
import org.example.backendwebapplication.iam.domain.model.aggregates.Account;
import org.example.backendwebapplication.iam.domain.model.aggregates.UserProfile;
import org.example.backendwebapplication.iam.domain.model.commands.SignInCommand;
import org.example.backendwebapplication.iam.domain.model.commands.SignUpCommand;
import org.example.backendwebapplication.iam.domain.model.commands.UpdateUserProfileCommand;
import org.example.backendwebapplication.iam.domain.model.valueobjects.EmailAddress;
import org.example.backendwebapplication.iam.domain.repositories.AccountRepository;
import org.example.backendwebapplication.iam.domain.repositories.UserProfileRepository;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link IamCommandService}.
 *
 * <p>Handles all write use cases for the IAM bounded context:
 * account registration, authentication, and profile updates.</p>
 */
@Service
@RequiredArgsConstructor
public class IamCommandServiceImpl implements IamCommandService {

    private final AccountRepository accountRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     *
     * <p>Business rules enforced:
     * <ul>
     *   <li>Email must be unique — returns ACCOUNT_CONFLICT if already registered.</li>
     *   <li>After account creation, a UserProfile is automatically created.</li>
     *   <li>Domain event {@code AccountCreatedEvent} is fired after persistence.</li>
     * </ul>
     */
    @Override
    @Transactional
    public Result<Account, ApplicationError> handle(SignUpCommand command) {
        var email = new EmailAddress(command.email());

        if (accountRepository.existsByEmail(email)) {
            return Result.failure(ApplicationError.conflict(
                    "ACCOUNT",
                    "Email already registered: " + command.email()));
        }

        var hashedPassword = passwordEncoder.encode(command.password());
        var account = new Account(command, hashedPassword);
        var savedAccount = accountRepository.save(account);
        savedAccount.onCreated();

        var profile = new UserProfile(
                savedAccount.getId(),
                command.fullName(),
                command.email(),
                command.photoUrl());
        userProfileRepository.save(profile);

        return Result.success(savedAccount);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Business rules enforced:
     * <ul>
     *   <li>Returns VALIDATION_ERROR if email not found or password does not match.</li>
     * </ul>
     */
    @Override
    @Transactional(readOnly = true)
    public Result<Account, ApplicationError> handle(SignInCommand command) {
        var email = new EmailAddress(command.email());
        var account = accountRepository.findByEmail(email).orElse(null);

        if (account == null || !passwordEncoder.matches(command.password(), account.getPasswordHash())) {
            return Result.failure(ApplicationError.validationError(
                    "credentials",
                    "Invalid email or password"));
        }

        return Result.success(account);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Business rules enforced:
     * <ul>
     *   <li>Returns PROFILE_NOT_FOUND if no profile exists for the given accountId.</li>
     * </ul>
     */
    @Override
    @Transactional
    public Result<UserProfile, ApplicationError> handle(UpdateUserProfileCommand command) {
        var profile = userProfileRepository.findByAccountId(command.accountId()).orElse(null);

        if (profile == null) {
            return Result.failure(ApplicationError.notFound(
                    "PROFILE",
                    "accountId=" + command.accountId()));
        }

        profile.update(command);
        var savedProfile = userProfileRepository.save(profile);
        return Result.success(savedProfile);
    }
}
