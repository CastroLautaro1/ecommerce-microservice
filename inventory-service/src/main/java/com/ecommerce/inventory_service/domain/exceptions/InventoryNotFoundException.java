package com.ecommerce.inventory_service.domain.exceptions;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(Long productId) {
        super("No se encontró un registro de inventario para el producto con ID: " + productId);
    }

    public InventoryNotFoundException(String sku) {
        super("No se encontró un registro de inventario para el SKU: " + sku);
    }
}
