package org.example.backendwebapplication.iam.application.internal.queryservices;

import org.example.backendwebapplication.iam.application.queryservices.UserQueryService;
import org.example.backendwebapplication.iam.domain.model.aggregates.User;
import org.example.backendwebapplication.iam.domain.model.queries.GetUserByEmailQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetUserByIdQuery;
import org.example.backendwebapplication.iam.domain.model.valueobjects.Email;
import org.example.backendwebapplication.iam.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link UserQueryService}.
 */
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(new Email(query.email()));
    }
}
