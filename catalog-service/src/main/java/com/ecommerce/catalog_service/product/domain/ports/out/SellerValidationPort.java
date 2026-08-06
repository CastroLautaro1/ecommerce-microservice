package com.ecommerce.catalog_service.product.domain.ports.out;

// Puerto para validar la existencia del vendedor
public interface SellerValidationPort {
    boolean isValidSeller(Long sellerId);
}
