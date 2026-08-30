package com.ecommerce.catalog_service.product.domain.ports.out;

import java.util.UUID;

// Puerto para validar la existencia del vendedor
public interface SellerValidationPort {
    boolean isValidSeller(UUID sellerId);
}
