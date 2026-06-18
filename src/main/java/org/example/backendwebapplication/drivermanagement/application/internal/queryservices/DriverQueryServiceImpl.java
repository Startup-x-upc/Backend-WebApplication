package org.example.backendwebapplication.drivermanagement.application.internal.queryservices;

import org.example.backendwebapplication.drivermanagement.application.queryservices.DriverQueryService;
import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.domain.model.queries.*;
import org.example.backendwebapplication.drivermanagement.domain.repositories.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link DriverQueryService}.
 */
@Service
public class DriverQueryServiceImpl implements DriverQueryService {

    private final DriverRepository driverRepository;

    public DriverQueryServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public Optional<Driver> handle(GetDriverByIdQuery query) {
        return driverRepository.findByDriverId(query.driverId());
    }

    @Override
    public Optional<Driver> handle(GetDriverByUserIdQuery query) {
        return driverRepository.findByUserId(query.userId());
    }

    @Override
    public List<Driver> handle(GetAllDriversQuery query) {
        return driverRepository.findAll(query.page(), query.perPage(), query.accessStatus());
    }

    @Override
    public long count(GetAllDriversQuery query) {
        return driverRepository.count(query.accessStatus());
    }
}
