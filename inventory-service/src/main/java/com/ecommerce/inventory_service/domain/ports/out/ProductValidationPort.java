package com.ecommerce.inventory_service.domain.ports.out;

import java.util.UUID;

// Puerto para validar la existencia del Producto y si le pertenece al vendedor
public interface ProductValidationPort {
    boolean isProductOwnedBySeller(Long productId, UUID sellerId);
}
