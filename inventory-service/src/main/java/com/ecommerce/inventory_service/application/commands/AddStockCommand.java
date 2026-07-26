package com.ecommerce.inventory_service.application.commands;

public record AddStockCommand(
        Long productId,
        int quantityToAdd
) {
}
