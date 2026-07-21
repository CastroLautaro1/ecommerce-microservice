package com.ecommerce.order_service.application.commands;

public record OrderItemCommand(
        Long productId,
        int quantity
) {
}
