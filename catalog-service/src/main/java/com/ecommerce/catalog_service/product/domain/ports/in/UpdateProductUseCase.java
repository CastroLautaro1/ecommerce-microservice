package com.ecommerce.catalog_service.product.domain.ports.in;

import com.ecommerce.catalog_service.product.application.commands.UpdateProductCommand;
import com.ecommerce.catalog_service.product.domain.models.Product;

public interface UpdateProductUseCase {

    Product execute(UpdateProductCommand command);
}
