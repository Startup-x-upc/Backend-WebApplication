package org.example.backendwebapplication.monetization.interfaces.rest.transform;

import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.WalletTransactionResponse;

import java.util.List;
import java.util.stream.Collectors;

public class WalletTransactionResourceAssembler {

    public static WalletTransactionResponse toResource(WalletTransaction transaction) {
        return new WalletTransactionResponse(
                transaction.getTransactionId(),
                transaction.getWalletId(),
                transaction.getTripId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getResultingBalance(),
                transaction.getTimestamp()
        );
    }

    public static List<WalletTransactionResponse> toResourceList(List<WalletTransaction> transactions) {
        return transactions.stream()
                .map(WalletTransactionResourceAssembler::toResource)
                .collect(Collectors.toList());
    }
}
