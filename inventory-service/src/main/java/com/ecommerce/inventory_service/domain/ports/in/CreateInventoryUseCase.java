package com.ecommerce.inventory_service.domain.ports.in;

import com.ecommerce.inventory_service.domain.models.Inventory;

public interface CreateInventoryUseCase {
    Inventory execute(CreateInventoryCommand command);
}
