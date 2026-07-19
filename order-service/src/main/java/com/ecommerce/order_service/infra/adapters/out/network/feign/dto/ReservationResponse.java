package com.ecommerce.order_service.infra.adapters.out.network.feign.dto;

import java.util.UUID;

public record ReservationResponse(
        UUID reservationId
) {
}
