package com.ecommerce.order_service.infra.adapters.in.web.dto;

import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        String status
) {
}
