package org.example.backendwebapplication.monetization.application.queryservices;

import org.example.backendwebapplication.monetization.domain.model.aggregates.FarePolicy;
import org.example.backendwebapplication.monetization.domain.model.aggregates.Wallet;
import org.example.backendwebapplication.monetization.domain.model.entities.WalletTransaction;
import org.example.backendwebapplication.monetization.domain.model.queries.*;

import java.math.BigDecimal;
import java.util.List;

public interface MonetizationQueryService {
    FarePolicy handle(GetCurrentFarePolicyQuery query);
    BigDecimal handle(GetEstimatedFareQuery query);
    Wallet handle(GetWalletByDriverIdQuery query);
    List<WalletTransaction> handle(GetWalletTransactionHistoryQuery query);
    boolean handle(CanDriverOperateQuery query);
}
