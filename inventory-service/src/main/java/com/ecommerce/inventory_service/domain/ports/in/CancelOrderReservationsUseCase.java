package com.ecommerce.inventory_service.domain.ports.in;

import java.util.UUID;

public interface CancelOrderReservationsUseCase {
    void execute(UUID orderId);
}
