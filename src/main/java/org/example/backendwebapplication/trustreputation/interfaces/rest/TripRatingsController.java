package org.example.backendwebapplication.trustreputation.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverContextFacade;
import org.example.backendwebapplication.shared.interfaces.rest.resources.ErrorResource;
import org.example.backendwebapplication.trustreputation.application.commandservices.TripRatingCommandService;
import org.example.backendwebapplication.trustreputation.application.queryservices.TripRatingQueryService;
import org.example.backendwebapplication.trustreputation.domain.model.commands.SubmitDriverRatingCommand;
import org.example.backendwebapplication.trustreputation.domain.model.commands.SubmitPassengerRatingCommand;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetDriverReputationQuery;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetPassengerReputationQuery;
import org.example.backendwebapplication.trustreputation.domain.model.queries.GetTripRatingByTripIdQuery;
import org.example.backendwebapplication.trustreputation.interfaces.rest.resources.DriverReputationResponse;
import org.example.backendwebapplication.trustreputation.interfaces.rest.resources.PassengerReputationResponse;
import org.example.backendwebapplication.trustreputation.interfaces.rest.resources.SubmitDriverRatingResource;
import org.example.backendwebapplication.trustreputation.interfaces.rest.resources.SubmitPassengerRatingResource;
import org.example.backendwebapplication.trustreputation.interfaces.rest.resources.TripRatingResponse;
import org.example.backendwebapplication.trustreputation.interfaces.rest.transform.TripRatingResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Trust & Reputation", description = "Trust & Reputation Bounded Context Endpoints")
public class TripRatingsController {

    private final TripRatingCommandService commandService;
    private final TripRatingQueryService queryService;
    private final DriverContextFacade driverContextFacade;

    public TripRatingsController(TripRatingCommandService commandService,
                                 TripRatingQueryService queryService,
                                 DriverContextFacade driverContextFacade) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.driverContextFacade = driverContextFacade;
    }

    private UUID getAuthenticatedUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }

    @PostMapping("/trips/{tripId}/rate-driver")
    @Operation(summary = "Submit Driver Rating (Passenger rates driver)")
    @ApiResponse(responseCode = "200", description = "Driver rated successfully",
                 content = @Content(schema = @Schema(implementation = TripRatingResponse.class)))
    public ResponseEntity<?> rateDriver(@PathVariable UUID tripId, @Valid @RequestBody SubmitDriverRatingResource resource) {
        UUID userId = getAuthenticatedUserId();
        var rating = queryService.handle(new GetTripRatingByTripIdQuery(tripId))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));

        if (!rating.getPassengerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No eres el pasajero de este ride"));
        }

        var result = commandService.handle(new SubmitDriverRatingCommand(tripId, resource.score()));
        return ResponseEntity.ok(TripRatingResourceAssembler.toResource(result));
    }

    @PostMapping("/trips/{tripId}/rate-passenger")
    @Operation(summary = "Submit Passenger Rating (Driver rates passenger)")
    @ApiResponse(responseCode = "200", description = "Passenger rated successfully",
                 content = @Content(schema = @Schema(implementation = TripRatingResponse.class)))
    public ResponseEntity<?> ratePassenger(@PathVariable UUID tripId, @Valid @RequestBody SubmitPassengerRatingResource resource) {
        UUID userId = getAuthenticatedUserId();
        var rating = queryService.handle(new GetTripRatingByTripIdQuery(tripId))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));

        UUID driverId = driverContextFacade.getDriverIdByUserId(userId).orElse(null);
        if (driverId == null || !rating.getDriverId().equals(driverId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No eres el conductor de este ride"));
        }

        var result = commandService.handle(new SubmitPassengerRatingCommand(tripId, resource.score(), resource.comment()));
        return ResponseEntity.ok(TripRatingResourceAssembler.toResource(result));
    }

    @PostMapping("/trips/{tripId}/skip-driver-rating")
    @Operation(summary = "Skip Driver Rating (Passenger skips driver rating)")
    @ApiResponse(responseCode = "200", description = "Driver rating skipped successfully",
                 content = @Content(schema = @Schema(implementation = TripRatingResponse.class)))
    public ResponseEntity<?> skipDriverRating(@PathVariable UUID tripId) {
        UUID userId = getAuthenticatedUserId();
        var rating = queryService.handle(new GetTripRatingByTripIdQuery(tripId))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));

        if (!rating.getPassengerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No eres el pasajero de este ride"));
        }

        var result = commandService.handleSkipDriverRating(tripId);
        return ResponseEntity.ok(TripRatingResourceAssembler.toResource(result));
    }

    @PostMapping("/trips/{tripId}/skip-passenger-rating")
    @Operation(summary = "Skip Passenger Rating (Driver skips passenger rating)")
    @ApiResponse(responseCode = "200", description = "Passenger rating skipped successfully",
                 content = @Content(schema = @Schema(implementation = TripRatingResponse.class)))
    public ResponseEntity<?> skipPassengerRating(@PathVariable UUID tripId) {
        UUID userId = getAuthenticatedUserId();
        var rating = queryService.handle(new GetTripRatingByTripIdQuery(tripId))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));

        UUID driverId = driverContextFacade.getDriverIdByUserId(userId).orElse(null);
        if (driverId == null || !rating.getDriverId().equals(driverId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No eres el conductor de este ride"));
        }

        var result = commandService.handleSkipPassengerRating(tripId);
        return ResponseEntity.ok(TripRatingResourceAssembler.toResource(result));
    }

    @GetMapping("/trips/{tripId}/rating")
    @Operation(summary = "Get Trip Rating details")
    @ApiResponse(responseCode = "200", description = "Trip rating details retrieved successfully",
                 content = @Content(schema = @Schema(implementation = TripRatingResponse.class)))
    public ResponseEntity<?> getTripRating(@PathVariable UUID tripId) {
        UUID userId = getAuthenticatedUserId();
        var rating = queryService.handle(new GetTripRatingByTripIdQuery(tripId))
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: TripRating no encontrado"));

        UUID driverId = driverContextFacade.getDriverIdByUserId(userId).orElse(null);
        if (!rating.getPassengerId().equals(userId) && (driverId == null || !rating.getDriverId().equals(driverId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No eres participante de este ride"));
        }

        return ResponseEntity.ok(TripRatingResourceAssembler.toResource(rating));
    }

    @GetMapping("/drivers/{driverId}/reputation")
    @Operation(summary = "Get Driver Reputation details")
    public ResponseEntity<DriverReputationResponse> getDriverReputation(@PathVariable UUID driverId) {
        var reputation = queryService.handle(new GetDriverReputationQuery(driverId));
        return ResponseEntity.ok(TripRatingResourceAssembler.toResource(reputation));
    }

    @GetMapping("/passengers/{passengerId}/reputation")
    @Operation(summary = "Get Passenger Reputation details")
    public ResponseEntity<PassengerReputationResponse> getPassengerReputation(@PathVariable UUID passengerId) {
        var reputation = queryService.handle(new GetPassengerReputationQuery(passengerId));
        return ResponseEntity.ok(TripRatingResourceAssembler.toResource(reputation));
    }
}
