package com.ecommerce.catalog_service.category.application.services;

import com.ecommerce.catalog_service.category.application.commands.RenameCategoryCommand;
import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;
import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.domain.ports.in.RenameCategoryUseCase;
import com.ecommerce.catalog_service.category.domain.ports.out.CategoryRepositoryPort;
import com.ecommerce.catalog_service.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RenameCategoryService implements RenameCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public RenameCategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category execute(RenameCategoryCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", command.categoryId()));

        // Si cambió el nombre, valido que no este repetido
        if (command.newName() != null && !command.newName().equalsIgnoreCase(category.getName())) {
            if (categoryRepository.existsByName(command.newName())) {
                throw new BusinessRuleViolationException("El nombre de la categoría ya está en uso: " + command.newName());
            }
        }

        category.updateName(command.newName());

        return categoryRepository.save(category);
    }
}
