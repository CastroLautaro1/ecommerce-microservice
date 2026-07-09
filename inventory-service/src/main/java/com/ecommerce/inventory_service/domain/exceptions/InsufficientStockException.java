package com.ecommerce.inventory_service.domain.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String sku, int requested, int available) {
        super(String.format("Stock insuficiente para el producto SKU [%s]. Cantidad solicitada: %d, Stock disponible: %d",
                sku, requested, available));
    }
}
