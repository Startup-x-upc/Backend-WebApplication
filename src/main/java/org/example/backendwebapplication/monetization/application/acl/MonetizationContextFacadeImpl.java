package org.example.backendwebapplication.monetization.application.acl;

import org.example.backendwebapplication.monetization.interfaces.acl.MonetizationContextFacade;
import org.example.backendwebapplication.monetization.application.queryservices.MonetizationQueryService;
import org.example.backendwebapplication.monetization.domain.model.queries.CanDriverOperateQuery;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementation of {@link MonetizationContextFacade}.
 * <p>Translates requests from external contexts into Monetization domain queries.</p>
 */
@Component
public class MonetizationContextFacadeImpl implements MonetizationContextFacade {

    private final MonetizationQueryService queryService;

    public MonetizationContextFacadeImpl(MonetizationQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public boolean hasDriverPositiveWalletBalance(UUID driverId) {
        try {
            return queryService.handle(new CanDriverOperateQuery(driverId, null));
        } catch (Exception e) {
            // If wallet is not found or other error, return false
            return false;
        }
    }
}
