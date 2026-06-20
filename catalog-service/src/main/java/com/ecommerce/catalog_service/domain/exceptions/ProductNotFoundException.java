package com.ecommerce.catalog_service.domain.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("El producto con ID " + productId + " no existe o fue eliminado.");
    }
}
