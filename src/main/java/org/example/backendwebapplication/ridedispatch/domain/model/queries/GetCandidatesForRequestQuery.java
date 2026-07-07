package org.example.backendwebapplication.ridedispatch.domain.model.queries;

import java.util.UUID;

public record GetCandidatesForRequestQuery(UUID requestId, UUID passengerId) {}
