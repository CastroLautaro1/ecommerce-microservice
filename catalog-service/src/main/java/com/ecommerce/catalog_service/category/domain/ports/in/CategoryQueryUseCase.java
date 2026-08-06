package com.ecommerce.catalog_service.category.domain.ports.in;

public interface CategoryQueryUseCase {
    boolean existsAndIsActive(Long categoryId);
}
