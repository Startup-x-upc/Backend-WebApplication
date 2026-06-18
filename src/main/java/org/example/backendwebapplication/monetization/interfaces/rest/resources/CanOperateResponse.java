package org.example.backendwebapplication.monetization.interfaces.rest.resources;

import java.util.UUID;

public record CanOperateResponse(
        UUID driverId,
        boolean canOperate
) {}