package org.example.backendwebapplication.iam.application.acl;

import org.example.backendwebapplication.iam.domain.model.valueobjects.UserRole;
import org.example.backendwebapplication.iam.domain.repositories.ProfileRepository;
import org.example.backendwebapplication.iam.domain.repositories.UserRepository;
import org.example.backendwebapplication.iam.interfaces.acl.IamContextFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link IamContextFacade}.
 * <p>Other bounded contexts inject this facade to validate identities
 * without depending on IAM's internal services or repositories directly.</p>
 *  *
 *  * <p>All methods are read-only and use the domain repositories directly
 *  * for efficiency .</p>
 */
@Service
@RequiredArgsConstructor
public class IamContextFacadeImpl implements IamContextFacade {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean existsUserById(UUID userId) {
        return userRepository.findById(userId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserRole> getUserRoleById(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getFullNameByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .map(profile -> profile.getFullName());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getPhotoUrlByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .map(profile -> profile.getPhotoUrl());
    }
}
