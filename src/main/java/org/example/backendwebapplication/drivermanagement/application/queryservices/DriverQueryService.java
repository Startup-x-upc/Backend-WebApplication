package org.example.backendwebapplication.drivermanagement.application.queryservices;

import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing the driver query service.
 */
public interface DriverQueryService {

    Optional<Driver> handle(GetDriverByIdQuery query);

    Optional<Driver> handle(GetDriverByUserIdQuery query);

    List<Driver> handle(GetAllDriversQuery query);

    long count(GetAllDriversQuery query);
}
