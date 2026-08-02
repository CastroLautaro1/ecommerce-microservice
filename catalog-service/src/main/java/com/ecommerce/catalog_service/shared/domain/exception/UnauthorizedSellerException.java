package com.ecommerce.catalog_service.shared.domain.exception;

public class UnauthorizedSellerException extends RuntimeException {
    public UnauthorizedSellerException(Long userId, Long productId) {
        super("El usuario " + userId + " no tiene permisos para modificar el producto " + productId + ".");
    }
}
