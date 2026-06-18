package org.example.backendwebapplication.monetization.domain.model.queries;

import java.math.BigDecimal;
import java.util.UUID;

public record CanDriverOperateQuery(
        UUID driverId,
        BigDecimal estimatedFare
) {}