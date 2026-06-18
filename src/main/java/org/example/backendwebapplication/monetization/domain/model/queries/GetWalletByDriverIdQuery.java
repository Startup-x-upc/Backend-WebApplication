package org.example.backendwebapplication.monetization.domain.model.queries;

import java.util.UUID;

public record GetWalletByDriverIdQuery(
        UUID driverId
) {}