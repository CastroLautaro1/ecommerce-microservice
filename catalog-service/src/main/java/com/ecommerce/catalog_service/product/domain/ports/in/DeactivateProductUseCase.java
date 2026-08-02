package com.ecommerce.catalog_service.product.domain.ports.in;

import com.ecommerce.catalog_service.product.application.commands.DeactivateProductCommand;

public interface DeactivateProductUseCase {

    void execute(DeactivateProductCommand command);
}
