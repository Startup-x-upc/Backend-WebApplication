package org.example.backendwebapplication.monetization.domain.model.queries;

import java.util.UUID;

public record CanDriverOperateQuery(
        UUID driverId
) {}