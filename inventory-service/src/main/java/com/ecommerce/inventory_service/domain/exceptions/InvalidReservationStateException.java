package com.ecommerce.inventory_service.domain.exceptions;

public class InvalidReservationStateException extends RuntimeException {
    public InvalidReservationStateException(String message) {
        super(message);
    }
}
