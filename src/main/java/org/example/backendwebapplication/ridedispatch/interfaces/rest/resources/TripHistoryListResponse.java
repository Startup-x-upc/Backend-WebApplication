package org.example.backendwebapplication.ridedispatch.interfaces.rest.resources;

import java.util.List;

public record TripHistoryListResponse(
        List<RideResponse> data,
        PaginationMeta meta
) {
    public record PaginationMeta(
            int page,
            int perPage,
            long total,
            int pages
    ) {}
}
