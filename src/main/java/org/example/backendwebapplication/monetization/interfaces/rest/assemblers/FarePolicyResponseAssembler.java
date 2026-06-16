package org.example.backendwebapplication.monetization.interfaces.rest.assemblers;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.FarePolicyResponse;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.FareQuoteResponse;

import java.math.BigDecimal;

public class FarePolicyResponseAssembler {

    public static FarePolicyResponse toResponse(FarePolicy farePolicy) {
        return new FarePolicyResponse(
                farePolicy.getBaseFare(),
                farePolicy.getPricePerKm(),
                farePolicy.getMinimumFare(),
                farePolicy.getCommissionRate()
        );
    }

    public static FareQuoteResponse toQuoteResponse(BigDecimal estimatedFare) {
        return new FareQuoteResponse(estimatedFare);
    }
}