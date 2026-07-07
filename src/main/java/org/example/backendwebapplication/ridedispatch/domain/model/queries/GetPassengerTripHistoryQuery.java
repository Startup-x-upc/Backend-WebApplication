package org.example.backendwebapplication.ridedispatch.domain.model.queries;

import java.util.UUID;

public record GetPassengerTripHistoryQuery(
        UUID passengerId,
        int page,
        int perPage,
        String status
) {}
