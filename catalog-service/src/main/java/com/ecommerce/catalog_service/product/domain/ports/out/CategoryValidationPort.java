package com.ecommerce.catalog_service.product.domain.ports.out;

public interface CategoryValidationPort {
    boolean existsAndIsActive(Long categoryId);
}
