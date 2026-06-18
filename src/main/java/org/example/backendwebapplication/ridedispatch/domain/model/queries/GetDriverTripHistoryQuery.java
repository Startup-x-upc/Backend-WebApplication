package org.example.backendwebapplication.ridedispatch.domain.model.queries;

import java.util.UUID;

public record GetDriverTripHistoryQuery(
        UUID driverId,
        int page,
        int perPage,
        String status
) {}
