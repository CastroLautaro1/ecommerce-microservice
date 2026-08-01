package com.ecommerce.catalog_service.category.application.commands;

public record MoveCategoryCommand(
        Long categoryId,
        Long newParentId
) {
}
