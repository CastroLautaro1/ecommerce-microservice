package com.ecommerce.inventory_service.domain.ports.in;

import java.util.UUID;

public interface CancelReservationUseCase {
    void execute(UUID reservationId);
}
