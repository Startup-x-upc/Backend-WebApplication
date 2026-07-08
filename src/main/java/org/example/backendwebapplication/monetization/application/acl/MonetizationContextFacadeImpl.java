package org.example.backendwebapplication.monetization.application.acl;

import org.example.backendwebapplication.monetization.interfaces.acl.MonetizationContextFacade;
import org.example.backendwebapplication.monetization.application.queryservices.MonetizationQueryService;
import org.example.backendwebapplication.monetization.domain.model.queries.CanDriverOperateQuery;
import org.example.backendwebapplication.monetization.domain.model.queries.GetCurrentFarePolicyQuery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementation of {@link MonetizationContextFacade}.
 * <p>Translates requests from external contexts into Monetization domain queries.</p>
 */
@Component
public class MonetizationContextFacadeImpl implements MonetizationContextFacade {

    private final MonetizationQueryService queryService;

    /**
     * Constructor for the facade implementation.
     * @summary Initializes the facade with the required query service.
     * @param queryService The monetization query service to be used.
     * @see MonetizationQueryService
     */
    public MonetizationContextFacadeImpl(MonetizationQueryService queryService) {
        this.queryService = queryService;
    }
    @Override
    /**
     * Checks if a driver has a positive wallet balance.
     * @summary Determines whether a driver can operate based on their wallet balance.
     * @param driverId The UUID of the driver to check.
     * @return true if the driver has a positive wallet balance, false otherwise.
     * @see CanDriverOperateQuery
     */
    public boolean hasDriverPositiveWalletBalance(UUID driverId) {
        try {
            return queryService.handle(new CanDriverOperateQuery(driverId, null));
        } catch (Exception e) {
            // If wallet is not found or other error, return false
            return false;
        }
    }

    @Override
    /**
     * Retrieves the current minimum fare.
     * @summary Gets the minimum fare from the current fare policy.
     * @return The minimum fare as BigDecimal, or BigDecimal.ZERO if an error occurs.
     * @see GetCurrentFarePolicyQuery
     */
    public BigDecimal getMinimumFare() {
        try {
            var policy = queryService.handle(new GetCurrentFarePolicyQuery());
            return policy.getMinimumFare();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
