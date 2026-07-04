package org.example.backendwebapplication.shared.infrastructure.realtime;

import org.example.backendwebapplication.monetization.domain.model.events.WalletEmptyEvent;
import org.example.backendwebapplication.ridedispatch.domain.model.events.*;
import org.example.backendwebapplication.shared.domain.services.RealtimePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AblyEventListener {
    private static final Logger log = LoggerFactory.getLogger(AblyEventListener.class);
    private final RealtimePublisher realtimePublisher;

    public AblyEventListener(RealtimePublisher realtimePublisher) {
        this.realtimePublisher = realtimePublisher;
    }

    /*
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = false)
    public void onRideRequestCreated(RideRequestCreatedEvent event) {
        try {
            realtimePublisher.publish("ride-request:open", "request.created", event);
        } catch (Exception e) {
            log.error("Error broadcasting RideRequestCreatedEvent to Ably: {}", e.getMessage());
        }
    }
    */

    /*
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = false)
    public void onDriverApplied(DriverAppliedEvent event) {
        try {
            realtimePublisher.publish("ride-request:" + event.requestId(), "candidate.applied", event);
        } catch (Exception e) {
            log.error("Error broadcasting DriverAppliedEvent to Ably: {}", e.getMessage());
        }
    }
    */

    /*
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = false)
    public void onRideAssigned(RideAssignedEvent event) {
        try {
            // Notify the selected driver (actor channel)
            realtimePublisher.publish("driver:" + event.driverId(), "ride.assigned", event);
            // Notify the passenger in the request channel (entity channel)
            realtimePublisher.publish("ride-request:" + event.requestId(), "ride.assigned", event);
        } catch (Exception e) {
            log.error("Error broadcasting RideAssignedEvent to Ably: {}", e.getMessage());
        }
    }
    */

    /*
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = false)
    public void onRideStatusAdvanced(RideStatusAdvancedEvent event) {
        try {
            realtimePublisher.publish("ride:" + event.rideId(), "ride.status-updated", event);
        } catch (Exception e) {
            log.error("Error broadcasting RideStatusAdvancedEvent to Ably: {}", e.getMessage());
        }
    }
    */

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = false)
    public void onRideCompleted(RideCompletedEvent event) {
        try {
            realtimePublisher.publish("ride:" + event.rideId(), "ride.completed", event);
        } catch (Exception e) {
            log.error("Error broadcasting RideCompletedEvent to Ably: {}", e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = false)
    public void onRideCancelled(RideCancelledEvent event) {
        try {
            realtimePublisher.publish("ride:" + event.rideId(), "ride.cancelled", event);
        } catch (Exception e) {
            log.error("Error broadcasting RideCancelledEvent to Ably: {}", e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = false)
    public void onWalletEmpty(WalletEmptyEvent event) {
        try {
            realtimePublisher.publish("driver:" + event.driverId(), "wallet.empty", event);
        } catch (Exception e) {
            log.error("Error broadcasting WalletEmptyEvent to Ably: {}", e.getMessage());
        }
    }
}
