package com.ecommerce.order_service.domain.ports.out;

import java.util.UUID;

public interface InventoryClientPort {
    // Intenta reservar el stock. Si falla, el adaptador lanzará una excepción (ej. 409 Conflict)
    UUID reserveStock(Long orderIdInternal, Long productId, int quantity);

    // Transacciones compensatorias o de finalización
    void confirmReservation(UUID reservationId);
    void cancelReservation(UUID reservationId);
}
