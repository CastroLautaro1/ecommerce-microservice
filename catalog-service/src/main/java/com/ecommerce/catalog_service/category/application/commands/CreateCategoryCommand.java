package com.ecommerce.catalog_service.category.application.commands;

public record CreateCategoryCommand(
        String name,
        Long parentId
) {
}
