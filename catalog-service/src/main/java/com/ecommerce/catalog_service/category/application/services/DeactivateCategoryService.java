package com.ecommerce.catalog_service.category.application.services;

import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.domain.ports.in.DeactivateCategoryUseCase;
import com.ecommerce.catalog_service.category.domain.ports.out.CategoryRepositoryPort;
import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;
import com.ecommerce.catalog_service.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateCategoryService implements DeactivateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public DeactivateCategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void execute(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));

        // Si la categoria tiene hijas activas no se puede desactivar
        if (categoryRepository.hasActiveSubcategories(category.getId())) {
            throw new BusinessRuleViolationException(
                    "No se puede desactivar la categoría porque contiene subcategorías activas. " +
                            "Reasigne o desactive las subcategorías primero."
            );
        }

        category.deactivate();

        categoryRepository.save(category);
    }
}
