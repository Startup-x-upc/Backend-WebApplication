package org.example.backendwebapplication.iam.application.internal.queryservices;

import org.example.backendwebapplication.iam.application.queryservices.IamQueryService;
import org.example.backendwebapplication.iam.domain.model.aggregates.Account;
import org.example.backendwebapplication.iam.domain.model.aggregates.UserProfile;
import org.example.backendwebapplication.iam.domain.model.queries.GetAccountByIdQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetAllProfilesQuery;
import org.example.backendwebapplication.iam.domain.model.queries.GetProfileByAccountIdQuery;
import org.example.backendwebapplication.iam.domain.repositories.AccountRepository;
import org.example.backendwebapplication.iam.domain.repositories.UserProfileRepository;
import org.example.backendwebapplication.shared.application.result.ApplicationError;
import org.example.backendwebapplication.shared.application.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link IamQueryService}.
 *
 * <p>Handles all read use cases for the IAM bounded context.</p>
 */
@Service
@RequiredArgsConstructor
public class IamQueryServiceImpl implements IamQueryService {

    private final AccountRepository accountRepository;
    private final UserProfileRepository userProfileRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Result<Account, ApplicationError> handle(GetAccountByIdQuery query) {
        return accountRepository.findById(query.id())
                .map(Result::<Account, ApplicationError>success)
                .orElse(Result.failure(ApplicationError.notFound("ACCOUNT", query.id().toString())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Result<UserProfile, ApplicationError> handle(GetProfileByAccountIdQuery query) {
        return userProfileRepository.findByAccountId(query.accountId())
                .map(Result::<UserProfile, ApplicationError>success)
                .orElse(Result.failure(ApplicationError.notFound(
                        "PROFILE", "accountId=" + query.accountId())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserProfile> handle(GetAllProfilesQuery query) {
        return userProfileRepository.findAll();
    }
}
