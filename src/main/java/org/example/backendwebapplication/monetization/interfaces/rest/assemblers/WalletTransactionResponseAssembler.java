package org.example.backendwebapplication.monetization.interfaces.rest.assemblers;

import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.interfaces.rest.resources.WalletTransactionResponse;

import java.util.List;
import java.util.stream.Collectors;

public class WalletTransactionResponseAssembler {

    public static WalletTransactionResponse toResponse(WalletTransaction transaction) {
        return new WalletTransactionResponse(
                transaction.getTransactionId(),
                transaction.getWalletId(),
                transaction.getTripId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getResultingBalance()
        );
    }

    public static List<WalletTransactionResponse> toResponseList(List<WalletTransaction> transactions) {
        return transactions.stream()
                .map(WalletTransactionResponseAssembler::toResponse)
                .collect(Collectors.toList());
    }
}