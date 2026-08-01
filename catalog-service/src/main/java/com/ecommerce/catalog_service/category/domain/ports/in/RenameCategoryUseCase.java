package com.ecommerce.catalog_service.category.domain.ports.in;

import com.ecommerce.catalog_service.category.application.commands.RenameCategoryCommand;
import com.ecommerce.catalog_service.category.domain.models.Category;

public interface RenameCategoryUseCase {
    Category execute(RenameCategoryCommand command);
}
