package com.ecommerce.inventory_service.domain.exceptions;

public class InvalidProductOwnershipException extends RuntimeException {
    public InvalidProductOwnershipException(String message) {
        super(message);
    }
}
