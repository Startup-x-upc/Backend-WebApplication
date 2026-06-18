package org.example.backendwebapplication.ridedispatch.interfaces.acl.events;

import org.example.backendwebapplication.drivermanagement.domain.model.events.DriverAvailabilityChangedEvent;
import org.example.backendwebapplication.ridedispatch.domain.model.aggregates.DriverAvailability;
import org.example.backendwebapplication.ridedispatch.domain.repositories.DriverAvailabilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener that handles {@link DriverAvailabilityChangedEvent} by synchronizing the local availability state.
 */
@Component("ridedispatchDriverAvailabilityChangedEventListener")
public class DriverAvailabilityChangedEventListener {

    private static final Logger log = LoggerFactory.getLogger(DriverAvailabilityChangedEventListener.class);
    private final DriverAvailabilityRepository driverAvailabilityRepository;

    public DriverAvailabilityChangedEventListener(DriverAvailabilityRepository driverAvailabilityRepository) {
        this.driverAvailabilityRepository = driverAvailabilityRepository;
    }

    @EventListener
    public void on(DriverAvailabilityChangedEvent event) {
        log.info("DriverAvailabilityChangedEvent received for driverId: {}, isAvailable: {}", event.driverId(), event.isAvailable());
        var availability = driverAvailabilityRepository.findByDriverId(event.driverId())
                .orElseGet(() -> {
                    log.info("Creating new DriverAvailability record for driverId: {}", event.driverId());
                    return new DriverAvailability(event.driverId());
                });

        availability.syncAvailability(event.isAvailable());
        driverAvailabilityRepository.save(availability);
        log.info("DriverAvailability synchronized for driverId: {}", event.driverId());
    }
}
