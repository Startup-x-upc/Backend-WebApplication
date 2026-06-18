package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import java.util.List;

public record RideCandidateListResponse(
        List<RideCandidateResponse> data
) {}
