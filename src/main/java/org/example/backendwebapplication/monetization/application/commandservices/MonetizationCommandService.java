package org.example.backendwebapplication.monetization.application.commandservices;

import org.example.backendwebapplication.monetization.application.WalletRechargeResult;
import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.model.commands.*;
import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;

public interface MonetizationCommandService {
    Wallet handle(CreateWalletCommand command);
    FarePolicy handle(ConfigureFarePolicyCommand command);
    WalletRechargeResult handle(TopUpWalletCommand command);
    WalletTransaction handle(RegisterTopUpFailureCommand command);
    WalletTransaction handle(ApplyRideCommissionCommand command);
}
