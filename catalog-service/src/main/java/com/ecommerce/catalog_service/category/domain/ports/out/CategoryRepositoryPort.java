package com.ecommerce.catalog_service.category.domain.ports.out;

import com.ecommerce.catalog_service.category.domain.models.Category;

import java.util.Optional;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(Long categoryId);
    boolean existsByName(String name);
    boolean existsById(Long categoryId);
    boolean existsByIdAndActiveTrue(Long categoryId);
    boolean hasActiveSubcategories(Long parentId);
}
