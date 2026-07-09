package com.ecommerce.inventory_service.domain.models;

public enum ReservationStatus {
    ACTIVE,     // Stock bloqueado esperando el pago de la orden
    CONFIRMED,  // Pago exitoso: el stock se descontó definitivamente del almacén
    CANCELLED,  // Orden cancelada por el usuario o pago rechazado
    EXPIRED     // El tiempo límite de pago se agotó y el stock se liberó automáticamente
}
