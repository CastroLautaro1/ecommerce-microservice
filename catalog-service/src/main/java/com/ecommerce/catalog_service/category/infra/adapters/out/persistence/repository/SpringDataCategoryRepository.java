package com.ecommerce.catalog_service.category.infra.adapters.out.persistence.repository;

import com.ecommerce.catalog_service.category.infra.adapters.out.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {

    boolean existsByName(String name);
    boolean existsByParentIdAndActiveTrue(Long parentId);
    boolean existsByIdAndActiveTrue(Long categoryId);
}
