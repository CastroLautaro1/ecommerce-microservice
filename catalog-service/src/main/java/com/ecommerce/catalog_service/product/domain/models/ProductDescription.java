package com.ecommerce.catalog_service.product.domain.models;

import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;

// Value Object para encapsular las reglas que debe tener la descripcion
public record ProductDescription(String value) {

    private static final int MIN_LENGTH = 20;
    private static final int MAX_LENGTH = 2000;

    public ProductDescription {
        value = sanitizeAndValidate(value);
    }

    private static String sanitizeAndValidate(String rawDescription) {
        if (rawDescription == null || rawDescription.isBlank()) {
            throw new BusinessRuleViolationException("La descripción del producto no puede ser nula o vacía.");
        }

        String sanitized = rawDescription.trim();

        if (sanitized.length() < MIN_LENGTH || sanitized.length() > MAX_LENGTH) {
            throw new BusinessRuleViolationException(
                    String.format("La descripción del producto debe tener entre %d y %d caracteres.", MIN_LENGTH, MAX_LENGTH)
            );
        }

        // Detecta intento de inyeccion de scripts (XSS)
        if (sanitized.contains("<script>") || sanitized.contains("javascript:")) {
            throw new BusinessRuleViolationException("La descripción contiene código HTML/Script no permitido.");
        }

        return sanitized;
    }
}
