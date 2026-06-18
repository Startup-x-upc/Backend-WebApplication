package org.example.backendwebapplication.drivermanagement.domain.model.queries;

import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;

public record GetAllDriversQuery(
        DriverAccessStatus accessStatus,
        int page,
        int perPage
) {}
