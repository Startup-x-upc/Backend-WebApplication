package org.example.backendwebapplication.drivermanagement.interfaces.rest.resources;

import java.util.List;

public record DriverListResponse(
        List<DriverResponse> data,
        PaginationMeta meta
) {
    public record PaginationMeta(
            int page,
            int perPage,
            long total,
            int pages
    ) {}
}
