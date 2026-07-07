package org.example.backendwebapplication.trustreputation.interfaces.acl.events;

import org.example.backendwebapplication.ridedispatch.application.queryservices.RideQueryService;
import org.example.backendwebapplication.ridedispatch.domain.model.queries.GetRideByIdQuery;
import org.example.backendwebapplication.ridedispatch.domain.model.events.RideCompletedEvent;
import org.example.backendwebapplication.trustreputation.application.commandservices.TripRatingCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("trustreputationRideCompletedEventListener")
public class RideCompletedEventListener {

    private static final Logger log = LoggerFactory.getLogger(RideCompletedEventListener.class);

    private final TripRatingCommandService commandService;
    private final RideQueryService rideQueryService;

    public RideCompletedEventListener(TripRatingCommandService commandService,
                                      RideQueryService rideQueryService) {
        this.commandService = commandService;
        this.rideQueryService = rideQueryService;
    }

    @EventListener
    public void onRideCompleted(RideCompletedEvent event) {
        log.info("RideCompletedEvent received in trustreputation context. Ride ID: {}, Driver ID: {}", event.rideId(), event.driverId());
        try {
            var ride = rideQueryService.handle(new GetRideByIdQuery(event.rideId()))
                    .orElseThrow(() -> new IllegalArgumentException("Ride not found in Ride Dispatch context"));

            commandService.handleCreateTripRating(ride.getId(), ride.getDriverId(), ride.getPassengerId());
            log.info("Successfully created TripRating for completed ride {}", ride.getId());
        } catch (Exception e) {
            log.error("Error creating TripRating for completed ride: {}", e.getMessage(), e);
        }
    }
}
