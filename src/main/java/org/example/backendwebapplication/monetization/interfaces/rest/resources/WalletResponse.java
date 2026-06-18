package org.example.backendwebapplication.monetization.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        UUID driverId,
        BigDecimal balance,
        String status
) {}