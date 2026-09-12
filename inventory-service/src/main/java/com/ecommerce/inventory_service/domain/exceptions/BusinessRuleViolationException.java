package com.ecommerce.inventory_service.domain.exceptions;

public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
