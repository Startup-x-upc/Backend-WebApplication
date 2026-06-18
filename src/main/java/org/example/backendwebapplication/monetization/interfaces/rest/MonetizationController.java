package org.example.backendwebapplication.monetization.interfaces.rest;

import org.example.backendwebapplication.monetization.application.commandservices.MonetizationCommandService;
import org.example.backendwebapplication.monetization.application.queryservices.MonetizationQueryService;
import org.example.backendwebapplication.monetization.domain.model.commands.*;
import org.example.backendwebapplication.monetization.domain.model.queries.*;
import org.example.backendwebapplication.monetization.interfaces.rest.transform.FarePolicyResourceAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.transform.WalletResourceAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.transform.WalletTransactionResourceAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/monetization")
@Tag(name = "Monetization", description = "Monetization Bounded Context Endpoints")
public class MonetizationController {

    private final MonetizationCommandService commandService;
    private final MonetizationQueryService queryService;

    public MonetizationController(MonetizationCommandService commandService,
                                  MonetizationQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    // Fare Config endpoints
    @GetMapping("/fare-config")
    public ResponseEntity<FarePolicyResponse> getCurrentFarePolicy() {
        var result = queryService.handle(new GetCurrentFarePolicyQuery());
        return ResponseEntity.ok(FarePolicyResourceAssembler.toResource(result));
    }

    @PutMapping("/fare-config")
    public ResponseEntity<FarePolicyResponse> configureFarePolicy(@RequestBody ConfigureFarePolicyResource resource) {
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
    public ResponseEntity<WalletResponse> getWalletByDriverId(@PathVariable UUID driverId) {
        var result = queryService.handle(new GetWalletByDriverIdQuery(driverId));
        return ResponseEntity.ok(WalletResourceAssembler.toResource(result));
    }

    @PostMapping("/wallets/{walletId}/recharge")
    public ResponseEntity<WalletRechargeResponse> rechargeWallet(@PathVariable UUID walletId, @RequestBody TopUpWalletResource resource) {
        var command = new TopUpWalletCommand(walletId, resource.amount());
        var result = commandService.handle(command);
        return ResponseEntity.ok(new WalletRechargeResponse(
                WalletResourceAssembler.toResource(result.wallet()),
                WalletTransactionResourceAssembler.toResource(result.transaction())
        ));
    }

    @PostMapping("/wallets/{walletId}/apply-commission")
    public ResponseEntity<WalletTransactionResponse> applyCommission(@PathVariable UUID walletId, @RequestBody ApplyCommissionResource resource) {
        var command = new ApplyRideCommissionCommand(walletId, resource.tripId(), resource.rideFare());
        var result = commandService.handle(command);
        return ResponseEntity.ok(WalletTransactionResourceAssembler.toResource(result));
    }

    @PostMapping("/wallets/{walletId}/top-up-failure")
    public ResponseEntity<WalletTransactionResponse> registerTopUpFailure(@PathVariable UUID walletId, @RequestBody TopUpFailureResource resource) {
        var command = new RegisterTopUpFailureCommand(walletId, resource.amount(), resource.reason());
        var result = commandService.handle(command);
        return ResponseEntity.ok(WalletTransactionResourceAssembler.toResource(result));
    }

    @GetMapping("/wallets/{walletId}/transactions")
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
}