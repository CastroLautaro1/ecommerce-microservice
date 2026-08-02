package com.ecommerce.catalog_service.category.application.services;

import com.ecommerce.catalog_service.category.application.commands.PromoteCategoryToRootCommand;
import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.domain.ports.in.PromoteCategoryToRootUseCase;
import com.ecommerce.catalog_service.category.domain.ports.out.CategoryRepositoryPort;
import com.ecommerce.catalog_service.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromoteCategoryToRootService implements PromoteCategoryToRootUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public PromoteCategoryToRootService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category execute(PromoteCategoryToRootCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", command.categoryId()));

        category.promoteToRoot();

        return categoryRepository.save(category);
    }
}
