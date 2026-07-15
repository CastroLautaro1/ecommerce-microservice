package com.ecommerce.order_service.application.commands;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID orderId, // Para idempotencia
        Long userId,
        List<OrderItemCommand> items
) {
}
