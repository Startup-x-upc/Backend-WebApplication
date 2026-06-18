package org.example.backendwebapplication.monetization.application;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;

public record WalletRechargeResult(
        Wallet wallet,
        WalletTransaction transaction
) {}
