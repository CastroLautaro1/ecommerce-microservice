package com.ecommerce.order_service.infra.adapters.out.network.feign.dto;

import java.math.BigDecimal;

public record CatalogProductResponse(
        Long id,
        String name,
        PriceDto price,
        boolean active
) {
    // Metodo para facilitar el mapeo hacia el dominio
    public BigDecimal getPriceAmount() {
        return price != null ? price.amount() : BigDecimal.ZERO;
    }
}

// DTO anidado para deserializar la respuesta del catalog service
record PriceDto(
        BigDecimal amount,
        String currency
) {}
