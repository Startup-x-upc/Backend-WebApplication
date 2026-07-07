package org.example.backendwebapplication.monetization.interfaces.rest;

import org.example.backendwebapplication.drivermanagement.interfaces.acl.DriverContextFacade;
import org.example.backendwebapplication.monetization.application.commandservices.MonetizationCommandService;
import org.example.backendwebapplication.monetization.application.queryservices.MonetizationQueryService;
import org.example.backendwebapplication.monetization.domain.model.commands.*;
import org.example.backendwebapplication.monetization.domain.model.queries.*;
import org.example.backendwebapplication.monetization.interfaces.rest.transform.FarePolicyResourceAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.transform.WalletResourceAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.transform.WalletTransactionResourceAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.*;
import org.example.backendwebapplication.shared.interfaces.rest.resources.ErrorResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/monetization")
@Tag(name = "Monetization", description = "Monetization Bounded Context Endpoints")
public class MonetizationController {

    private final MonetizationCommandService commandService;
    private final MonetizationQueryService queryService;
    private final DriverContextFacade driverContextFacade;

    public MonetizationController(MonetizationCommandService commandService,
                                  MonetizationQueryService queryService,
                                  DriverContextFacade driverContextFacade) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.driverContextFacade = driverContextFacade;
    }

    // Fare Config endpoints
    @GetMapping("/fare-config")
    public ResponseEntity<FarePolicyResponse> getCurrentFarePolicy() {
        var result = queryService.handle(new GetCurrentFarePolicyQuery());
        return ResponseEntity.ok(FarePolicyResourceAssembler.toResource(result));
    }

    @PutMapping("/fare-config")
    @Operation(summary = "Configure global fare policy (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Fare policy updated successfully",
                 content = @Content(schema = @Schema(implementation = FarePolicyResponse.class)))
    public ResponseEntity<?> configureFarePolicy(@RequestBody ConfigureFarePolicyResource resource) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo administradores pueden configurar tarifas"));
        }
        var command = new ConfigureFarePolicyCommand(
                resource.baseFare(),
                resource.pricePerKm(),
                resource.minimumFare(),
                resource.commissionRate());
        var result = commandService.handle(command);
        return ResponseEntity.ok(FarePolicyResourceAssembler.toResource(result));
    }

    @PostMapping("/fare-config/estimate")
    public ResponseEntity<FareQuoteResponse> estimateFare(@RequestBody EstimatedFareResource resource) {
        var result = queryService.handle(new GetEstimatedFareQuery(resource.distanceKm()));
        return ResponseEntity.ok(FarePolicyResourceAssembler.toResource(result));
    }

    // Wallet endpoints
    @GetMapping("/drivers/{driverId}/wallet")
    @Operation(summary = "Get wallet by driver ID")
    @ApiResponse(responseCode = "200", description = "Wallet retrieved successfully",
                 content = @Content(schema = @Schema(implementation = WalletResponse.class)))
    public ResponseEntity<WalletResponse> getWalletByDriverId(@PathVariable UUID driverId) {
        var result = queryService.handle(new GetWalletByDriverIdQuery(driverId));
        return ResponseEntity.ok(WalletResourceAssembler.toResource(result));
    }

    @PostMapping("/wallets/{walletId}/recharge")
    @Operation(summary = "Recharge a driver wallet")
    @ApiResponse(responseCode = "200", description = "Wallet recharged successfully",
                 content = @Content(schema = @Schema(implementation = WalletRechargeResponse.class)))
    public ResponseEntity<?> rechargeWallet(@PathVariable UUID walletId, @RequestBody TopUpWalletResource resource) {
        if (!isAdmin() && !isWalletOwner(walletId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "No tienes permisos para recargar esta wallet"));
        }
        var command = new TopUpWalletCommand(walletId, resource.amount());
        var result = commandService.handle(command);
        return ResponseEntity.ok(new WalletRechargeResponse(
                WalletResourceAssembler.toResource(result.wallet()),
                WalletTransactionResourceAssembler.toResource(result.transaction())
        ));
    }

    @PostMapping("/wallets/{walletId}/apply-commission")
    public ResponseEntity<?> applyCommission(@PathVariable UUID walletId, @RequestBody ApplyCommissionResource resource) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo administradores pueden aplicar comisiones"));
        }
        var command = new ApplyRideCommissionCommand(walletId, resource.tripId(), resource.rideFare());
        var result = commandService.handle(command);
        return ResponseEntity.ok(WalletTransactionResourceAssembler.toResource(result));
    }

    @PostMapping("/wallets/{walletId}/top-up-failure")
    public ResponseEntity<?> registerTopUpFailure(@PathVariable UUID walletId, @RequestBody TopUpFailureResource resource) {
        if (!isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResource("FORBIDDEN", "Solo administradores pueden registrar fallos de recarga"));
        }
        var command = new RegisterTopUpFailureCommand(walletId, resource.amount(), resource.reason());
        var result = commandService.handle(command);
        return ResponseEntity.ok(WalletTransactionResourceAssembler.toResource(result));
    }

    @GetMapping("/wallets/{walletId}/transactions")
    @Operation(summary = "Get transaction history for a wallet")
    @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully",
                 content = @Content(schema = @Schema(implementation = WalletTransactionResponse.class)))
    public ResponseEntity<?> getTransactionHistory(@PathVariable UUID walletId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        var result = queryService.handle(new GetWalletTransactionHistoryQuery(walletId, page, size));
        return ResponseEntity.ok(WalletTransactionResourceAssembler.toResourceList(result));
    }

    @GetMapping("/wallets/{driverId}/can-operate")
    public ResponseEntity<CanOperateResponse> canDriverOperate(
            @PathVariable UUID driverId,
            @RequestParam(required = false) BigDecimal estimatedFare) {
        var result = queryService.handle(new CanDriverOperateQuery(driverId, estimatedFare));
        return ResponseEntity.ok(new CanOperateResponse(driverId, result));
    }

    // ── Private Helper Methods ──────────────────────────────────────────

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

    private boolean isWalletOwner(UUID walletId) {
        UUID userId = getAuthenticatedUserId();
        if (userId == null) return false;
        UUID driverId = driverContextFacade.getDriverIdByUserId(userId).orElse(null);
        if (driverId == null) return false;
        var wallet = queryService.handle(new GetWalletByDriverIdQuery(driverId));
        return wallet != null && wallet.getWalletId().equals(walletId);
    }
}