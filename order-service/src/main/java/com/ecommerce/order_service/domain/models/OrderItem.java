package com.ecommerce.order_service.domain.models;

import com.ecommerce.order_service.domain.exceptions.DomainValidationException;

import java.math.BigDecimal;

public class OrderItem {
    private final Long productId;
    private final String productName; // Snapshot del catálogo
    private final BigDecimal unitPrice; // Snapshot del precio cuando se hizo la compra
    private final int quantity;

    public OrderItem(Long productId, String productName, BigDecimal unitPrice, int quantity) {
        if (productId == null) throw new DomainValidationException("El ID del producto es obligatorio");
        if (productName == null || productName.isBlank()) throw new DomainValidationException("El nombre del producto es obligatorio");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) throw new DomainValidationException("El precio unitario debe ser mayor a cero");
        if (quantity <= 0) throw new DomainValidationException("La cantidad debe ser mayor a cero");

        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // Lógica de negocio encapsulada
    public BigDecimal calculateSubTotal() {
        return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
}
