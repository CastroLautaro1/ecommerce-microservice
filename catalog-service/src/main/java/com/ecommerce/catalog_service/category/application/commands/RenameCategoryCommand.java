package com.ecommerce.catalog_service.category.application.commands;

public record RenameCategoryCommand(
        Long categoryId,
        String newName
) {
}
