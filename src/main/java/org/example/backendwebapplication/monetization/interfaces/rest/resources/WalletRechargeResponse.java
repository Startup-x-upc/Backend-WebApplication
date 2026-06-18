package org.example.backendwebapplication.monetization.interfaces.rest.resources;

public record WalletRechargeResponse(
        WalletResponse wallet,
        WalletTransactionResponse transaction
) {}
