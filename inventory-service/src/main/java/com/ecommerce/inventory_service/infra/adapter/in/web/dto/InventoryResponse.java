package com.ecommerce.inventory_service.infra.adapter.in.web.dto;

import com.ecommerce.inventory_service.domain.models.Inventory;

public record InventoryResponse(
        Long inventoryId,
        Long productId,
        String sku,
        int totalStock,
        int availableStock
) {
    // Factory method para mapear desde el dominio de forma limpia
    public static InventoryResponse fromDomain(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getSku(),
                inventory.getTotalStock(),
                inventory.getAvailableStock()
        );
    }
}
