package org.example.backendwebapplication.ridedispatch.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverContextFacade;
import org.example.backendwebapplication.iam.interfaces.acl.IamContextFacade;
import org.example.backendwebapplication.ridedispatch.application.commandservices.RideCommandService;
import org.example.backendwebapplication.ridedispatch.application.queryservices.RideQueryService;
import org.example.backendwebapplication.ridedispatch.domain.model.commands.*;
import org.example.backendwebapplication.ridedispatch.domain.model.queries.*;
import org.example.backendwebapplication.ridedispatch.interfaces.rest.resources.*;
import org.example.backendwebapplication.ridedispatch.interfaces.rest.transform.RideResourceAssembler;
import org.example.backendwebapplication.shared.interfaces.rest.resources.ErrorResource;
import org.example.backendwebapplication.shared.interfaces.rest.transform.ErrorResponseAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Ride Dispatch", description = "Ride Dispatch Bounded Context Endpoints")
public class RidesController {

    private final RideCommandService commandService;
    private final RideQueryService queryService;
    private final DriverContextFacade driverContextFacade;
    private final IamContextFacade iamContextFacade;

    public RidesController(RideCommandService commandService,
                           RideQueryService queryService,
                           DriverContextFacade driverContextFacade,
                           IamContextFacade iamContextFacade) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.driverContextFacade = driverContextFacade;
        this.iamContextFacade = iamContextFacade;
    }

    private String getPassengerName(UUID passengerId) {
        return iamContextFacade.getFullNameByUserId(passengerId).orElse("Passenger");
    }

    private String getPassengerPhotoUrl(UUID passengerId) {
        return iamContextFacade.getPhotoUrlByUserId(passengerId).orElse("");
    }

    private String getDriverName(UUID driverId) {
        return driverContextFacade.getDriverDetails(driverId)
                .map(d -> d.fullName())
                .orElse("Driver");
    }

    private boolean isPassenger() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PASSENGER"));
    }

    private boolean isDriver() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DRIVER"));
    }

    private boolean isAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
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

    // ── 1. Create Ride Request ───────────────────────────────────────
    @PostMapping("/rides/requests")
    @Operation(summary = "Create a ride request")
    @ApiResponse(responseCode = "201", description = "Ride request created successfully",
                 content = @Content(schema = @Schema(implementation = RideRequestResponse.class)))
    public ResponseEntity<?> createRideRequest(@Valid @RequestBody CreateRideRequestResource resource) {
        if (!isPassenger() && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo pasajeros pueden crear solicitudes"));
        }
        UUID passengerId = getAuthenticatedUserId();
        var result = commandService.handle(new CreateRideRequestCommand(
                passengerId,
                resource.origin(),
                resource.destination(),
                resource.distanceKm(),
                resource.estimatedFare()
        ));
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(result.failure().get());
        }
        var request = result.success().get();
        String name = getPassengerName(passengerId);
        String photo = getPassengerPhotoUrl(passengerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RideResourceAssembler.toResource(request, name, photo));
    }

    // ── 2. Apply as Candidate ─────────────────────────────────────────
    @PostMapping("/rides/requests/{requestId}/candidates")
    @Operation(summary = "Apply as a candidate driver to a ride request")
    @ApiResponse(responseCode = "201", description = "Applied as candidate successfully",
                 content = @Content(schema = @Schema(implementation = RideCandidateResponse.class)))
    public ResponseEntity<?> applyAsCandidate(@PathVariable UUID requestId,
                                              @Valid @RequestBody(required = false) Object body) {
        if (!isDriver() && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo conductores pueden aplicar"));
        }
        UUID userId = getAuthenticatedUserId();
        UUID driverId = driverContextFacade.getDriverIdByUserId(userId).orElse(null);
        if (driverId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("DRIVER_NOT_FOUND", "No se encontró registro de conductor para este usuario"));
        }
        var result = commandService.handle(new ApplyAsCandidateCommand(requestId, driverId));
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(result.failure().get());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RideResourceAssembler.toResource(result.success().get()));
    }

    // ── 3. Select Candidate ────────────────────────────────────────────
    @PostMapping("/rides/requests/{requestId}/select")
    @Operation(summary = "Select a driver candidate for the ride request")
    @ApiResponse(responseCode = "201", description = "Driver selected successfully",
                 content = @Content(schema = @Schema(implementation = SelectCandidateResponse.class)))
    public ResponseEntity<?> selectCandidate(@PathVariable UUID requestId,
                                             @Valid @RequestBody SelectCandidateResource resource) {
        if (!isPassenger() && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No tienes permisos para esta acción"));
        }
        UUID passengerId = getAuthenticatedUserId();
        UUID candidateId = UUID.fromString(resource.candidateId());
        var result = commandService.handle(new SelectCandidateCommand(requestId, passengerId, candidateId));
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(result.failure().get());
        }
        var ride = result.success().get();
        String passName = getPassengerName(ride.getPassengerId());
        String drvName = getDriverName(ride.getDriverId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SelectCandidateResponse(RideResourceAssembler.toResource(ride, passName, drvName)));
    }

    // ── 4. Advance Ride Status ────────────────────────────────────────
    @PostMapping("/rides/{rideId}/advance")
    @Operation(summary = "Advance the status of the ride")
    @ApiResponse(responseCode = "200", description = "Ride status advanced successfully",
                 content = @Content(schema = @Schema(implementation = RideResponse.class)))
    public ResponseEntity<?> advanceRideStatus(@PathVariable UUID rideId,
                                               @Valid @RequestBody AdvanceRideStatusResource resource) {
        if (!isDriver() && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo el conductor asignado puede avanzar el estado"));
        }
        UUID userId = getAuthenticatedUserId();
        UUID driverId = driverContextFacade.getDriverIdByUserId(userId).orElse(null);
        if (driverId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("DRIVER_NOT_FOUND", "No se encontró registro de conductor"));
        }
        var result = commandService.handle(new AdvanceRideStatusCommand(rideId, driverId, resource.status()));
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(result.failure().get());
        }
        var ride = result.success().get();
        String passName = getPassengerName(ride.getPassengerId());
        String drvName = getDriverName(ride.getDriverId());
        return ResponseEntity.ok(RideResourceAssembler.toResource(ride, passName, drvName));
    }

    // ── 5. Cancel Ride ────────────────────────────────────────────────
    @PostMapping("/rides/{rideId}/cancel")
    @Operation(summary = "Cancel the ride")
    @ApiResponse(responseCode = "200", description = "Ride cancelled successfully",
                 content = @Content(schema = @Schema(implementation = RideResponse.class)))
    public ResponseEntity<?> cancelRide(@PathVariable UUID rideId) {
        UUID userId = getAuthenticatedUserId();
        UUID requesterId = userId;
        if (isDriver()) {
            requesterId = driverContextFacade.getDriverIdByUserId(userId).orElse(userId);
        }
        var result = commandService.handle(new CancelRideCommand(rideId, requesterId));
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(result.failure().get());
        }
        var ride = result.success().get();
        String passName = getPassengerName(ride.getPassengerId());
        String drvName = getDriverName(ride.getDriverId());
        return ResponseEntity.ok(RideResourceAssembler.toResource(ride, passName, drvName));
    }

    // ── 6. Get Open Ride Requests ────────────────────────────────────
    @GetMapping("/rides/requests")
    @Operation(summary = "Get open ride requests")
    @ApiResponse(responseCode = "200", description = "Open ride requests retrieved successfully",
                 content = @Content(schema = @Schema(implementation = RideRequestListResponse.class)))
    public ResponseEntity<?> getOpenRideRequests(@RequestParam(required = false) String status) {
        if (!isDriver() && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo conductores pueden consultar las solicitudes abiertas"));
        }
        var requests = queryService.handle(new GetOpenRideRequestsQuery());
        var responses = requests.stream()
                .map(r -> {
                    String name = getPassengerName(r.getPassengerId());
                    String photo = getPassengerPhotoUrl(r.getPassengerId());
                    return RideResourceAssembler.toResource(r, name, photo);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(new RideRequestListResponse(responses));
    }

    // ── 7. Get Ride Request By ID ─────────────────────────────────────
    @GetMapping("/rides/requests/{requestId}")
    @Operation(summary = "Get a ride request by ID")
    @ApiResponse(responseCode = "200", description = "Ride request retrieved successfully",
                 content = @Content(schema = @Schema(implementation = RideRequestResponse.class)))
    public ResponseEntity<?> getRideRequestById(@PathVariable UUID requestId) {
        var opt = queryService.handle(new GetRideRequestByIdQuery(requestId));
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResource("RIDE_REQUEST_NOT_FOUND", "Solicitud de viaje no encontrada"));
        }
        var request = opt.get();
        String name = getPassengerName(request.getPassengerId());
        String photo = getPassengerPhotoUrl(request.getPassengerId());
        return ResponseEntity.ok(RideResourceAssembler.toResource(request, name, photo));
    }

    // ── 8. Get Candidates for Request ─────────────────────────────────
    @GetMapping("/rides/requests/{requestId}/candidates")
    @Operation(summary = "Get candidates for a ride request")
    @ApiResponse(responseCode = "200", description = "Candidates retrieved successfully",
                 content = @Content(schema = @Schema(implementation = RideCandidateListResponse.class)))
    public ResponseEntity<?> getCandidatesForRequest(@PathVariable UUID requestId) {
        UUID passengerId = getAuthenticatedUserId();
        var candidates = queryService.handle(new GetCandidatesForRequestQuery(requestId, passengerId));
        var responses = candidates.stream()
                .map(RideResourceAssembler::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new RideCandidateListResponse(responses));
    }

    // ── 9. Get Driver Active Candidate ────────────────────────────────
    @GetMapping("/drivers/{driverId}/active-candidate")
    @Operation(summary = "Get driver active candidate application")
    @ApiResponse(responseCode = "200", description = "Active candidate retrieved successfully",
                 content = @Content(schema = @Schema(implementation = RideCandidateResponse.class)))
    public ResponseEntity<?> getDriverActiveCandidate(@PathVariable UUID driverId) {
        UUID userId = getAuthenticatedUserId();
        UUID loggedInDriverId = driverContextFacade.getDriverIdByUserId(userId).orElse(null);
        if (!driverId.equals(loggedInDriverId) && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No puedes consultar datos de otro conductor"));
        }
        var opt = queryService.handle(new GetDriverActiveCandidateQuery(driverId));
        if (opt.isEmpty()) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(RideResourceAssembler.toResource(opt.get()));
    }

    // ── 10. Get Active Ride for Driver ───────────────────────────────
    @GetMapping("/drivers/{driverId}/active-ride")
    @Operation(summary = "Get active ride for a driver")
    @ApiResponse(responseCode = "200", description = "Active ride retrieved successfully",
                 content = @Content(schema = @Schema(implementation = RideResponse.class)))
    public ResponseEntity<?> getActiveRideForDriver(@PathVariable UUID driverId) {
        UUID userId = getAuthenticatedUserId();
        UUID loggedInDriverId = driverContextFacade.getDriverIdByUserId(userId).orElse(null);
        if (!driverId.equals(loggedInDriverId) && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No puedes consultar datos de otro conductor"));
        }
        var opt = queryService.handle(new GetActiveRideForDriverQuery(driverId));
        if (opt.isEmpty()) {
            return ResponseEntity.ok(null);
        }
        var ride = opt.get();
        String passName = getPassengerName(ride.getPassengerId());
        String drvName = getDriverName(ride.getDriverId());
        return ResponseEntity.ok(RideResourceAssembler.toResource(ride, passName, drvName));
    }

    // ── 11. Get Ride By ID ────────────────────────────────────────────
    @GetMapping("/rides/{rideId}")
    @Operation(summary = "Get a ride by ID")
    @ApiResponse(responseCode = "200", description = "Ride retrieved successfully",
                 content = @Content(schema = @Schema(implementation = RideResponse.class)))
    public ResponseEntity<?> getRideById(@PathVariable UUID rideId) {
        var opt = queryService.handle(new GetRideByIdQuery(rideId));
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResource("RIDE_NOT_FOUND", "Viaje no encontrado"));
        }
        var ride = opt.get();
        String passName = getPassengerName(ride.getPassengerId());
        String drvName = getDriverName(ride.getDriverId());
        return ResponseEntity.ok(RideResourceAssembler.toResource(ride, passName, drvName));
    }

    // ── 12. Get Passenger Trip History ────────────────────────────────
    @GetMapping("/passengers/{passengerId}/trips")
    @Operation(summary = "Get passenger trip history")
    @ApiResponse(responseCode = "200", description = "Passenger trip history retrieved successfully",
                 content = @Content(schema = @Schema(implementation = TripHistoryListResponse.class)))
    public ResponseEntity<?> getPassengerTripHistory(@PathVariable UUID passengerId,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int perPage) {
        var query = new GetPassengerTripHistoryQuery(passengerId, page, perPage, status);
        var trips = queryService.handle(query);
        long total = queryService.count(query);
        int pages = (int) Math.ceil((double) total / perPage);

        var responses = trips.stream()
                .map(t -> {
                    String passName = getPassengerName(t.getPassengerId());
                    String drvName = getDriverName(t.getDriverId());
                    return RideResourceAssembler.toResource(t, passName, drvName);
                })
                .collect(Collectors.toList());

        var meta = new TripHistoryListResponse.PaginationMeta(page, perPage, total, pages);
        return ResponseEntity.ok(new TripHistoryListResponse(responses, meta));
    }

    // ── 13. Get Driver Trip History ───────────────────────────────────
    @GetMapping("/drivers/{driverId}/trips")
    @Operation(summary = "Get driver trip history")
    @ApiResponse(responseCode = "200", description = "Driver trip history retrieved successfully",
                 content = @Content(schema = @Schema(implementation = TripHistoryListResponse.class)))
    public ResponseEntity<?> getDriverTripHistory(@PathVariable UUID driverId,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int perPage) {
        var query = new GetDriverTripHistoryQuery(driverId, page, perPage, status);
        var trips = queryService.handle(query);
        long total = queryService.count(query);
        int pages = (int) Math.ceil((double) total / perPage);

        var responses = trips.stream()
                .map(t -> {
                    String passName = getPassengerName(t.getPassengerId());
                    String drvName = getDriverName(t.getDriverId());
                    return RideResourceAssembler.toResource(t, passName, drvName);
                })
                .collect(Collectors.toList());

        var meta = new TripHistoryListResponse.PaginationMeta(page, perPage, total, pages);
        return ResponseEntity.ok(new TripHistoryListResponse(responses, meta));
    }

    // ── 14. Get Driver Availability ──────────────────────────────────
    @GetMapping("/drivers/{driverId}/availability")
    @Operation(summary = "Get driver availability details")
    @ApiResponse(responseCode = "200", description = "Driver availability retrieved successfully",
                 content = @Content(schema = @Schema(implementation = DriverAvailabilityResponse.class)))
    public ResponseEntity<?> getDriverAvailability(@PathVariable UUID driverId) {
        var opt = queryService.handle(new GetDriverAvailabilityQuery(driverId));
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResource("AVAILABILITY_NOT_FOUND", "No se encontró registro de disponibilidad"));
        }
        return ResponseEntity.ok(RideResourceAssembler.toResource(opt.get()));
    }
}
