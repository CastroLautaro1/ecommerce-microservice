package com.ecommerce.inventory_service.domain.exceptions;

import java.util.UUID;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(UUID reservationId) {
        super("No se encontró ninguna reserva con el ID: " + reservationId);
    }
}
