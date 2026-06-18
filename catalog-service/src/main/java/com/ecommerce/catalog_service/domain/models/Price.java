package com.ecommerce.catalog_service.domain.models;

import java.math.BigDecimal;

public record Price(BigDecimal amount, String currency) {
    public Price {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            // Lanzamos una excepción pura de Java. En infraestructura,
            // un @ControllerAdvice la transformará en un HTTP 409 Conflict.
            throw new IllegalArgumentException("El precio no puede ser negativo o nulo");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }
    }
}
