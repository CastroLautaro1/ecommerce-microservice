package com.ecommerce.catalog_service.product.infra.adapters.out.external;

import com.ecommerce.catalog_service.category.domain.ports.in.CategoryQueryUseCase;
import com.ecommerce.catalog_service.product.domain.ports.out.CategoryValidationPort;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidationAdapter implements CategoryValidationPort {

    private final CategoryQueryUseCase categoryQuery;

    public CategoryValidationAdapter(CategoryQueryUseCase categoryQuery) {
        this.categoryQuery = categoryQuery;
    }

    @Override
    public boolean existsAndIsActive(Long categoryId) {
        return categoryQuery.existsAndIsActive(categoryId);
    }
}
