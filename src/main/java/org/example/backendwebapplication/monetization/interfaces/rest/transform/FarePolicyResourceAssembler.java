package org.example.backendwebapplication.monetization.interfaces.rest.transform;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.FarePolicyResponse;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.FareQuoteResponse;

import java.math.BigDecimal;

public class FarePolicyResourceAssembler {

    public static FarePolicyResponse toResource(FarePolicy farePolicy) {
        return new FarePolicyResponse(
                farePolicy.getFarePolicyId(),
                farePolicy.getBaseFare(),
                farePolicy.getPricePerKm(),
                farePolicy.getMinimumFare(),
                farePolicy.getCommissionRate(),
                farePolicy.getUpdatedAt()
        );
    }

    public static FareQuoteResponse toResource(BigDecimal estimatedFare) {
        return new FareQuoteResponse(estimatedFare);
    }
}
