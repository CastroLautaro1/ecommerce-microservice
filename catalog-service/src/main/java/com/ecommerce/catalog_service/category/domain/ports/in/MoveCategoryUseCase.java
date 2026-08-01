package com.ecommerce.catalog_service.category.domain.ports.in;

import com.ecommerce.catalog_service.category.application.commands.MoveCategoryCommand;
import com.ecommerce.catalog_service.category.domain.models.Category;

public interface MoveCategoryUseCase {
    Category execute(MoveCategoryCommand command);
}
