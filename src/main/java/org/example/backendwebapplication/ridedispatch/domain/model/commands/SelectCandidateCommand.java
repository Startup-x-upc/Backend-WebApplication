package org.example.backendwebapplication.ridedispatch.domain.model.commands;

import java.util.UUID;

public record SelectCandidateCommand(
        UUID requestId,
        UUID candidateId,
        UUID passengerId
) {}
