package org.example.backendwebapplication.iam.interfaces.rest.transform;

import org.example.backendwebapplication.iam.domain.model.aggregates.Account;
import org.example.backendwebapplication.iam.interfaces.rest.resources.AuthenticatedAccountResource;

/**
 * Assembler that converts {@link Account} domain aggregates
 * to REST response resources.
 */
public final class AccountResourceAssembler {

    private AccountResourceAssembler() {}

    public static AuthenticatedAccountResource toResourceFromDomain(Account account) {
        return new AuthenticatedAccountResource(
                account.getId(),
                account.getEmail(),
                account.getRole());
    }
}
