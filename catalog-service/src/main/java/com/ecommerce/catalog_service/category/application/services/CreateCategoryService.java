package com.ecommerce.catalog_service.category.application.services;

import com.ecommerce.catalog_service.category.application.commands.CreateCategoryCommand;
import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;
import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.domain.ports.in.CreateCategoryUseCase;
import com.ecommerce.catalog_service.category.domain.ports.out.CategoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCategoryService implements CreateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public CreateCategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category execute(CreateCategoryCommand command) {
        if (categoryRepository.existsByName(command.name())) {
            throw new BusinessRuleViolationException("El nombre de la categoría ya existe: " + command.name());
        }

        if (command.parentId() != null && !categoryRepository.existsById(command.parentId())) {
            throw new BusinessRuleViolationException("La categoría padre con ID " + command.parentId() + " no existe.");
        }

        Category category = Category.registerCategory(command.name(), command.parentId());

        return categoryRepository.save(category);
    }
}
