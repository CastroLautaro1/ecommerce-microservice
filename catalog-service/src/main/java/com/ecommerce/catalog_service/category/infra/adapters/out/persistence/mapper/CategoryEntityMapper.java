package com.ecommerce.catalog_service.category.infra.adapters.out.persistence.mapper;

import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.infra.adapters.out.persistence.entity.CategoryJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryEntityMapper {

    CategoryJpaEntity toEntity(Category domain);

    Category toDomain(CategoryJpaEntity entity);
}
