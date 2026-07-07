package org.example.backendwebapplication.ridedispatch.domain.model.commands;

import java.util.UUID;

public record WithdrawCandidateCommand(
        UUID requestId,
        UUID driverId
) {}
