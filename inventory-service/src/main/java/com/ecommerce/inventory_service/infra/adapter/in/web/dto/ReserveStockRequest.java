package com.ecommerce.inventory_service.infra.adapter.in.web.dto;

public record ReserveStockRequest(
        Long orderId,
        Long productId,
        int quantity
) {
}
