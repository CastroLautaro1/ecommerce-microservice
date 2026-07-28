package com.ecommerce.inventory_service.application.commands;

import java.util.List;
import java.util.UUID;

public record ReserveStockCommand(
        UUID orderId,
        List<ItemReservationCommand> items
) {
}

