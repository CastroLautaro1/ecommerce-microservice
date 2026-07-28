package com.ecommerce.catalog_service.domain.ports.in;

import com.ecommerce.catalog_service.application.commands.DeactivateProductCommand;

public interface DeactivateProductUseCase {

    void execute(DeactivateProductCommand command);
}
