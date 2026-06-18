package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record SelectCandidateResource(
        @NotBlank String candidateId
) {}
