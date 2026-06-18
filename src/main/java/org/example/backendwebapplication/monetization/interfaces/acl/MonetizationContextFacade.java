package org.example.backendwebapplication.monetization.interfaces.acl;

import java.util.UUID;

/**
 * Anti-Corruption Layer (ACL) facade for the Monetization bounded context.
 * <p>Exposes a minimal, stable contract for other bounded contexts
 * (e.g. Driver Management) to query wallet status and balance information
 * without coupling directly to Monetization's internal query or command services.</p>
 */
public interface MonetizationContextFacade {

    /**
     * Checks whether a driver has a wallet with a positive balance.
     *
     * @param driverId the driver's business identifier (User ID)
     * @return {@code true} if the driver's wallet exists and has a balance > 0
     */
    boolean hasDriverPositiveWalletBalance(UUID driverId);

    /**
     * Gets the minimum fare from the currently configured fare policy.
     *
     * @return the minimum fare amount
     */
    java.math.BigDecimal getMinimumFare();
}
