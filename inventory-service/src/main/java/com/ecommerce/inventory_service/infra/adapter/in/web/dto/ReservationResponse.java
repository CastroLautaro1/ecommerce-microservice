package com.ecommerce.inventory_service.infra.adapter.in.web.dto;

import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        String message
) {
}
