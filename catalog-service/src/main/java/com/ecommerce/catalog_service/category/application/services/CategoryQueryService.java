package com.ecommerce.catalog_service.category.application.services;

import com.ecommerce.catalog_service.category.domain.ports.in.CategoryQueryUseCase;
import com.ecommerce.catalog_service.category.domain.ports.out.CategoryRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CategoryQueryService implements CategoryQueryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public CategoryQueryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean existsAndIsActive(Long categoryId) {
        return categoryRepository.existsByIdAndActiveTrue(categoryId);
    }
}
