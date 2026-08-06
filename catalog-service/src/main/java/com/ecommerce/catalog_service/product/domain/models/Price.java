package com.ecommerce.catalog_service.product.domain.models;

import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;

import java.math.BigDecimal;

public record Price(BigDecimal amount, String currency) {
    public Price {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException("El precio no puede ser negativo o nulo");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new BusinessRuleViolationException("La moneda es obligatoria");
        }
    }
}
