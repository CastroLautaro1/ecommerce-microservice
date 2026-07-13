package com.ecommerce.inventory_service.domain.ports.in;

public record AddStockCommand(
        Long productId,
        int quantityToAdd
) {
}
