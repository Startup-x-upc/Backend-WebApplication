package org.example.backendwebapplication.iam.application.internal.queryservices;

import org.example.backendwebapplication.iam.application.queryservices.ProfileQueryService;
import org.example.backendwebapplication.iam.domain.model.entities.Profile;
import org.example.backendwebapplication.iam.domain.model.queries.GetProfileByUserIdQuery;
import org.example.backendwebapplication.iam.domain.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link ProfileQueryService}.
 */
@Service
@RequiredArgsConstructor
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId());
    }
}
