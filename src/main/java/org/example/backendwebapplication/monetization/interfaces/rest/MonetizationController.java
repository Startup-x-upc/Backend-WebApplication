package org.example.backendwebapplication.monetization.interfaces.rest;

import org.example.backendwebapplication.monetization.application.MonetizationCommandServiceImpl;
import org.example.backendwebapplication.monetization.application.MonetizationQueryServiceImpl;
import org.example.backendwebapplication.monetization.domain.model.commands.*;
import org.example.backendwebapplication.monetization.domain.model.queries.*;
import org.example.backendwebapplication.monetization.interfaces.rest.assemblers.FarePolicyResponseAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.assemblers.WalletResponseAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.assemblers.WalletTransactionResponseAssembler;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/monetization")
public class MonetizationController {

    private final MonetizationCommandServiceImpl commandService;
    private final MonetizationQueryServiceImpl queryService;

    public MonetizationController(MonetizationCommandServiceImpl commandService,
                                  MonetizationQueryServiceImpl queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping("/fare-policy")
    public ResponseEntity<FarePolicyResponse> getCurrentFarePolicy() {
        var result = queryService.handle(new GetCurrentFarePolicyQuery());
        return ResponseEntity.ok(FarePolicyResponseAssembler.toResponse(result));
    }

    @PostMapping("/fare-policy")
    public ResponseEntity<FarePolicyResponse> configureFarePolicy(@RequestBody ConfigureFarePolicyResource resource) {
        var command = new ConfigureFarePolicyCommand(
                resource.baseFare(),
                resource.pricePerKm(),
                resource.minimumFare(),
                resource.commissionRate());
        var result = commandService.handle(command);
        return ResponseEntity.ok(FarePolicyResponseAssembler.toResponse(result));
    }

    @PostMapping("/fare-policy/estimate")
    public ResponseEntity<FareQuoteResponse> estimateFare(@RequestBody EstimatedFareResource resource) {
        var result = queryService.handle(new GetEstimatedFareQuery(resource.distanceKm()));
        return ResponseEntity.ok(FarePolicyResponseAssembler.toQuoteResponse(result));
    }

    @GetMapping("/wallets")
    public ResponseEntity<WalletResponse> getWalletByDriverId(@RequestParam UUID driverId) {
        var result = queryService.handle(new GetWalletByDriverIdQuery(driverId));
        return ResponseEntity.ok(WalletResponseAssembler.toResponse(result));
    }

    @PostMapping("/wallets/top-up")
    public ResponseEntity<WalletResponse> topUpWallet(@RequestBody TopUpWalletResource resource) {
        var command = new TopUpWalletCommand(resource.driverId(), resource.amount());
        var result = commandService.handle(command);
        return ResponseEntity.ok(WalletResponseAssembler.toResponse(result));
    }

    @PostMapping("/wallets/top-up-failure")
    public ResponseEntity<WalletTransactionResponse> registerTopUpFailure(@RequestBody TopUpFailureResource resource) {
        var command = new RegisterTopUpFailureCommand(resource.driverId(), resource.amount(), resource.reason());
        var result = commandService.handle(command);
        return ResponseEntity.ok(WalletTransactionResponseAssembler.toResponse(result));
    }

    @PostMapping("/wallets/commission")
    public ResponseEntity<WalletTransactionResponse> applyCommission(@RequestBody ApplyCommissionResource resource) {
        var command = new ApplyRideCommissionCommand(resource.driverId(), resource.tripId(), resource.rideFare());
        var result = commandService.handle(command);
        return ResponseEntity.ok(WalletTransactionResponseAssembler.toResponse(result));
    }

    @PostMapping("/wallets/{driverId}/block")
    public ResponseEntity<WalletResponse> blockWallet(@PathVariable UUID driverId) {
        var result = commandService.handle(new BlockDriverWalletCommand(driverId));
        return ResponseEntity.ok(WalletResponseAssembler.toResponse(result));
    }

    @PostMapping("/wallets/{driverId}/unblock")
    public ResponseEntity<WalletResponse> unblockWallet(@PathVariable UUID driverId) {
        var result = commandService.handle(new UnblockDriverWalletCommand(driverId));
        return ResponseEntity.ok(WalletResponseAssembler.toResponse(result));
    }

    @GetMapping("/wallets/{driverId}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable UUID driverId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        var result = queryService.handle(new GetWalletTransactionHistoryQuery(driverId, page, size));
        return ResponseEntity.ok(WalletTransactionResponseAssembler.toResponseList(result));
    }

    @GetMapping("/wallets/{driverId}/can-operate")
    public ResponseEntity<CanOperateResponse> canDriverOperate(@PathVariable UUID driverId) {
        var result = queryService.handle(new CanDriverOperateQuery(driverId));
        return ResponseEntity.ok(new CanOperateResponse(driverId, result));
    }
}