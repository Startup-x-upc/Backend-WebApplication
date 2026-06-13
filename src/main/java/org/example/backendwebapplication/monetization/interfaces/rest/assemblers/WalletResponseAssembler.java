package org.example.backendwebapplication.monetization.interfaces.rest.assemblers;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.WalletResponse;

public class WalletResponseAssembler {

    public static WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getWalletId(),
                wallet.getDriverId(),
                wallet.getBalance(),
                wallet.getStatus().name()
        );
    }
}