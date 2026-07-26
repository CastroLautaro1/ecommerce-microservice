package com.ecommerce.order_service.domain.models;

public enum OrderStatus {
    PENDING,    // Orden creada, stock reservado temporalmente, esperando pago.
    CONFIRMED,  // Pago exitoso, inventario notificado para descuento físico.
    CANCELLED,   // Fallo en el pago, fraude, o expiración de la reserva.
    REJECTED // Fallo al momento de reservas stock
}
