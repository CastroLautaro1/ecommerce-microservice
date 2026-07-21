package com.ecommerce.order_service.domain.ports.out;

import java.math.BigDecimal;
import java.util.Optional;

public interface CatalogClientPort {
    // Retorna un record/DTO interno de la capa de aplicación con los datos del snapshot
    Optional<ProductSnapshot> getProductSnapshot(Long productId);

    // DTO auxiliar para aislar los datos del catálogo
    record ProductSnapshot(Long productId, String name, BigDecimal currentPrice) {}
}
