package org.example.backendwebapplication.drivermanagement.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.backendwebapplication.drivermanagement.application.commandservices.DriverCommandService;
import org.example.backendwebapplication.drivermanagement.application.queryservices.DriverQueryService;
import org.example.backendwebapplication.drivermanagement.domain.model.aggregates.Driver;
import org.example.backendwebapplication.drivermanagement.domain.model.commands.RestrictDriverCommand;
import org.example.backendwebapplication.drivermanagement.domain.model.commands.ToggleDriverAvailabilityCommand;
import org.example.backendwebapplication.drivermanagement.domain.model.commands.UnrestrictDriverCommand;
import org.example.backendwebapplication.drivermanagement.domain.model.queries.GetAllDriversQuery;
import org.example.backendwebapplication.drivermanagement.domain.model.queries.GetDriverByIdQuery;
import org.example.backendwebapplication.drivermanagement.domain.model.queries.GetDriverByUserIdQuery;
import org.example.backendwebapplication.drivermanagement.domain.model.valueobjects.DriverAccessStatus;
import org.example.backendwebapplication.drivermanagement.interfaces.rest.resources.DriverListResponse;
import org.example.backendwebapplication.drivermanagement.interfaces.rest.resources.DriverResponse;
import org.example.backendwebapplication.drivermanagement.interfaces.rest.resources.RestrictDriverResource;
import org.example.backendwebapplication.drivermanagement.interfaces.rest.transform.DriverResourceAssembler;
import org.example.backendwebapplication.shared.interfaces.rest.resources.ErrorResource;
import org.example.backendwebapplication.shared.interfaces.rest.transform.ErrorResponseAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for Driver Management Bounded Context.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Driver Management", description = "Driver Management Bounded Context Endpoints")
public class DriversController {

    private final DriverCommandService commandService;
    private final DriverQueryService queryService;

    public DriversController(DriverCommandService commandService,
                             DriverQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    // ── Toggle Availability ───────────────────────────────────────────

    @PostMapping("/drivers/{id}/toggle-availability")
    @Operation(summary = "Toggle driver availability status")
    public ResponseEntity<?> toggleAvailability(@PathVariable UUID id) {
        var driverOpt = queryService.handle(new GetDriverByIdQuery(id));
        if (driverOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResource("DRIVER_NOT_FOUND", "Driver not found"));
        }

        var driver = driverOpt.get();
        if (!isOwnDriver(driver) && !isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No tienes permisos para modificar este conductor"));
        }

        var result = commandService.handle(new ToggleDriverAvailabilityCommand(id));
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(result.failure().get());
        }

        return ResponseEntity.ok(DriverResourceAssembler.toResource(result.success().get()));
    }

    // ── Restrict Driver (Admin) ───────────────────────────────────────

    @PostMapping("/drivers/{id}/restrict")
    @Operation(summary = "Restrict a driver (ADMIN only)")
    public ResponseEntity<?> restrictDriver(@PathVariable UUID id,
                                            @Valid @RequestBody RestrictDriverResource resource) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo administradores pueden realizar esta acción"));
        }

        var result = commandService.handle(new RestrictDriverCommand(id, resource.reason()));
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(result.failure().get());
        }

        return ResponseEntity.ok(DriverResourceAssembler.toResource(result.success().get()));
    }

    // ── Unrestrict Driver (Admin) ─────────────────────────────────────

    @PostMapping("/drivers/{id}/unrestrict")
    @Operation(summary = "Unrestrict a driver (ADMIN only)")
    public ResponseEntity<?> unrestrictDriver(@PathVariable UUID id) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo administradores pueden realizar esta acción"));
        }

        var result = commandService.handle(new UnrestrictDriverCommand(id));
        if (result.isFailure()) {
            return ErrorResponseAssembler.toErrorResponseFromApplicationError(result.failure().get());
        }

        return ResponseEntity.ok(DriverResourceAssembler.toResource(result.success().get()));
    }

    // ── Get All Drivers (Admin) ───────────────────────────────────────

    @GetMapping("/drivers")
    @Operation(summary = "Get all drivers (ADMIN only)")
    public ResponseEntity<?> getAllDrivers(
            @RequestParam(required = false) String accessStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {

        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo administradores pueden listar conductores"));
        }

        DriverAccessStatus statusEnum = null;
        if (accessStatus != null && !accessStatus.trim().isEmpty()) {
            try {
                statusEnum = DriverAccessStatus.valueOf(accessStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResource("INVALID_ACCESS_STATUS", "Invalid access status value"));
            }
        }

        // Convert 1-based page query param to 0-based Spring Pageable index
        int jpaPage = Math.max(0, page - 1);
        int size = Math.max(1, perPage);

        var query = new GetAllDriversQuery(statusEnum, jpaPage, size);
        var list = queryService.handle(query);
        long total = queryService.count(query);
        int totalPages = (int) Math.ceil((double) total / size);

        var resources = list.stream()
                .map(DriverResourceAssembler::toResource)
                .collect(Collectors.toList());

        var response = new DriverListResponse(
                resources,
                new DriverListResponse.PaginationMeta(page, size, total, totalPages)
        );

        return ResponseEntity.ok(response);
    }

    // ── Get Driver By ID ──────────────────────────────────────────────

    @GetMapping("/drivers/{id}")
    @Operation(summary = "Get driver by ID")
    public ResponseEntity<?> getDriverById(@PathVariable UUID id) {
        var driver = queryService.handle(new GetDriverByIdQuery(id));
        if (driver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResource("DRIVER_NOT_FOUND", "Driver not found"));
        }
        return ResponseEntity.ok(DriverResourceAssembler.toResource(driver.get()));
    }

    // ── Get Driver By User ID ─────────────────────────────────────────

    @GetMapping("/users/{userId}/driver")
    @Operation(summary = "Get driver by user ID")
    public ResponseEntity<?> getDriverByUserId(@PathVariable UUID userId) {
        var driver = queryService.handle(new GetDriverByUserIdQuery(userId));
        if (driver.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResource("DRIVER_NOT_FOUND", "Driver not found"));
        }
        return ResponseEntity.ok(DriverResourceAssembler.toResource(driver.get()));
    }

    // ── Private Helper Methods ────────────────────────────────────────

    private boolean isAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isOwnDriver(Driver driver) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        Object principal = auth.getPrincipal();
        if (principal instanceof UUID requesterUserId) {
            return driver.getUserId().equals(requesterUserId);
        }
        return false;
    }
}
