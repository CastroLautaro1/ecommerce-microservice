package com.ecommerce.catalog_service.shared.domain.exception;

import java.util.UUID;

public class UnauthorizedSellerException extends RuntimeException {
    public UnauthorizedSellerException(UUID userId, Long productId) {
        super("El usuario " + userId + " no tiene permisos para modificar el producto " + productId + ".");
    }
}
