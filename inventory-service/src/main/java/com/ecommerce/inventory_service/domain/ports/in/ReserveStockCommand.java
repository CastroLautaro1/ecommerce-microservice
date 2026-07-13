package com.ecommerce.inventory_service.domain.ports.in;

public record ReserveStockCommand(
        Long orderId,
        Long productId,
        int quantity
) {
}
