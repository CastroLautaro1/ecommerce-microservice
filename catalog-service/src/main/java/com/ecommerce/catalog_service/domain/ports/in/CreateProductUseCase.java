package com.ecommerce.catalog_service.domain.ports.in;

import com.ecommerce.catalog_service.domain.models.Product;

public interface CreateProductUseCase {

    Product execute(CreateProductCommand command);
}
