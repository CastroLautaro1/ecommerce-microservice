package com.ecommerce.catalog_service.domain.ports.in;

public interface DeactivateProductUseCase {

    void execute(DeactivateProductCommand command);
}
