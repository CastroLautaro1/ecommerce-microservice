package com.ecommerce.catalog_service.category.domain.ports.in;

import com.ecommerce.catalog_service.category.application.commands.PromoteCategoryToRootCommand;
import com.ecommerce.catalog_service.category.domain.models.Category;

public interface PromoteCategoryToRootUseCase {
    Category execute(PromoteCategoryToRootCommand command);
}
