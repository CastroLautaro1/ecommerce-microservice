package com.ecommerce.catalog_service.shared.domain.exception;

public class ExternalServiceIntegrationException extends RuntimeException {
    public ExternalServiceIntegrationException(String message, Exception e) {
        super(message);
    }
}
