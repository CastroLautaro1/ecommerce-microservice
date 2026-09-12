package com.ecommerce.inventory_service.domain.ports.in;

import com.ecommerce.inventory_service.application.commands.CreateInventoryCommand;
import com.ecommerce.inventory_service.domain.models.Inventory;

import java.util.UUID;

public interface CreateInventoryUseCase {
    Inventory execute(CreateInventoryCommand command, UUID sellerId);
}
