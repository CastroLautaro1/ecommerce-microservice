package com.ecommerce.catalog_service.category.infra.adapters.in.web.dto;

public record CreateCategoryRequest(
        String name,
        Long parentId
) {
}
