package com.ecommerce.catalog_service.category.domain.ports.in;

import com.ecommerce.catalog_service.category.application.commands.CreateCategoryCommand;
import com.ecommerce.catalog_service.category.domain.models.Category;

public interface CreateCategoryUseCase {
    Category execute(CreateCategoryCommand command);
}
