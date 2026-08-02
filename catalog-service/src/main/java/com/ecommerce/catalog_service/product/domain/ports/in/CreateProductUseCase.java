package com.ecommerce.catalog_service.product.domain.ports.in;

import com.ecommerce.catalog_service.product.application.commands.CreateProductCommand;
import com.ecommerce.catalog_service.product.domain.models.Product;

public interface CreateProductUseCase {

    Product execute(CreateProductCommand command);
}
