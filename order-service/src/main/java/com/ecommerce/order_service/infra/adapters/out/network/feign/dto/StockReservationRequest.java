package com.ecommerce.order_service.infra.adapters.out.network.feign.dto;

import java.util.List;
import java.util.UUID;

public record StockReservationRequest(
        UUID orderId,
        List<StockItemRequest> items
) {
}
