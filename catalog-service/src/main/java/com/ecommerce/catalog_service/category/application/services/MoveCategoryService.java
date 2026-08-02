package com.ecommerce.catalog_service.category.application.services;

import com.ecommerce.catalog_service.category.application.commands.MoveCategoryCommand;
import com.ecommerce.catalog_service.category.domain.services.CategoryHierarchyValidator;
import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;
import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.domain.ports.in.MoveCategoryUseCase;
import com.ecommerce.catalog_service.category.domain.ports.out.CategoryRepositoryPort;
import com.ecommerce.catalog_service.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoveCategoryService implements MoveCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;
    private final CategoryHierarchyValidator hierarchyValidator;

    public MoveCategoryService(CategoryRepositoryPort categoryRepository, CategoryHierarchyValidator hierarchyValidator) {
        this.categoryRepository = categoryRepository;
        this.hierarchyValidator = hierarchyValidator;
    }

    @Override
    @Transactional
    public Category execute(MoveCategoryCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", command.categoryId()));

        if (!categoryRepository.existsByIdAndActiveTrue(command.newParentId())) {
            throw new BusinessRuleViolationException("La categoría padre destino no existe o fue dada de baja.");
        }

        // Delegación de invariante estructural compleja
        hierarchyValidator.validateNoCycles(command.categoryId(), command.newParentId());

        // Mutación pura
        category.changeParent(command.newParentId());

        return categoryRepository.save(category);
    }
}
