package com.ecommerce.inventory_service.application.commands;

public record ItemReservationCommand(
        Long productId,
        int quantity
) {}
