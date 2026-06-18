package org.example.backendwebapplication.monetization.interfaces.rest.transform;

import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.WalletResponse;

public class WalletResourceAssembler {

    public static WalletResponse toResource(Wallet wallet) {
        return new WalletResponse(
                wallet.getWalletId(),
                wallet.getDriverId(),
                wallet.getBalance(),
                wallet.getStatus().name()
        );
    }
}
