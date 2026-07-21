package com.ecommerce.inventory_service.domain.ports.in;

public record ItemReservationCommand(
        Long productId,
        int quantity
) {}
