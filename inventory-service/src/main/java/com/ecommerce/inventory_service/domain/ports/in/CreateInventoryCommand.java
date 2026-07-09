package com.ecommerce.inventory_service.domain.ports.in;

public record CreateInventoryCommand(
        Long productId,
        String sku,
        int initialStock
) {
}
