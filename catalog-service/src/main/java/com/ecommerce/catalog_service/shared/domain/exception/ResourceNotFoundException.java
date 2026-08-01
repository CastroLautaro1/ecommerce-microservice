package com.ecommerce.catalog_service.shared.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
    // Construye un mensaje estandarizado
    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(String.format("El recurso '%s' con ID %d no existe o fue eliminado.", resourceName, resourceId));
    }

    // Constructor alternativo por si la búsqueda no fue por ID
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
