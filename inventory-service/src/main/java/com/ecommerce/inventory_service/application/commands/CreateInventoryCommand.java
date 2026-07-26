package com.ecommerce.inventory_service.application.commands;

public record CreateInventoryCommand(
        Long productId,
        String sku,
        int initialStock
) {
}
