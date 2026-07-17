package com.ecommerce.order_service.domain.ports.out;

import com.ecommerce.order_service.domain.models.OrderItem;

import java.util.List;
import java.util.UUID;

public interface InventoryClientPort {
    // Intenta reservar el stock. Si falla, el adaptador lanzará una excepción (ej. 409 Conflict);
    UUID reserveStock(UUID orderId, List<OrderItem> items);

    // Transacciones compensatorias o de finalización
    void confirmReservation(UUID reservationId);
    void cancelReservation(UUID reservationId);
}
