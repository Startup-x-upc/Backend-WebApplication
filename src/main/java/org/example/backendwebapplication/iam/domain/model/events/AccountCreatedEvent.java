package org.example.backendwebapplication.iam.domain.model.events;

import org.example.backendwebapplication.iam.domain.model.aggregates.Account;

/**
 * Domain event published when a new {@link Account} is successfully created and persisted.
 *
 * <p>Other bounded contexts (e.g. ride-dispatch) can listen to this event
 * to react to account creation without directly coupling to the IAM
 * application services.</p>
 *
 * @param accountId the ID of the newly created account
 * @param email     the email of the new account
 * @param role      the role assigned to the new account
 */
public record AccountCreatedEvent(Long accountId, String email, String role) {

    /**
     * Convenience factory that extracts all needed fields from a saved {@link Account}.
     *
     * @param account the saved account (must already carry a non-null id)
     * @return a fully populated {@link AccountCreatedEvent}
     */
    public static AccountCreatedEvent from(Account account) {
        return new AccountCreatedEvent(
                account.getId(),
                account.getEmail(),
                account.getRole().name());
    }
}
