package com.ecommerce.order_service.domain.exceptions;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("No se encontró la orden con el ID interno: " + id);
    }

    public OrderNotFoundException(UUID orderId) {
        super("No se encontró la orden con el UUID de seguimiento: " + orderId);
    }
}
