package org.example.backendwebapplication.ridedispatch.domain.model.commands;

import java.util.UUID;

public record ApplyAsCandidateCommand(
        UUID requestId,
        UUID driverId
) {}
