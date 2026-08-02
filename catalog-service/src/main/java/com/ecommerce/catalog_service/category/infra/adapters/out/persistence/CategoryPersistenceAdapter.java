package com.ecommerce.catalog_service.category.infra.adapters.out.persistence;

import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.domain.ports.out.CategoryRepositoryPort;
import com.ecommerce.catalog_service.category.infra.adapters.out.persistence.entity.CategoryJpaEntity;
import com.ecommerce.catalog_service.category.infra.adapters.out.persistence.mapper.CategoryEntityMapper;
import com.ecommerce.catalog_service.category.infra.adapters.out.persistence.repository.SpringDataCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final SpringDataCategoryRepository categoryRepository;
    private final CategoryEntityMapper mapper;

    public CategoryPersistenceAdapter(SpringDataCategoryRepository categoryRepository, CategoryEntityMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = mapper.toEntity(category);
        CategoryJpaEntity savedEntity = categoryRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        return categoryRepository.findById(categoryId).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    public boolean existsByIdAndActiveTrue(Long categoryId) {
        return categoryRepository.existsByIdAndActiveTrue(categoryId);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public boolean hasActiveSubcategories(Long parentId) {
        return categoryRepository.existsByParentIdAndActiveTrue(parentId);
    }
}
