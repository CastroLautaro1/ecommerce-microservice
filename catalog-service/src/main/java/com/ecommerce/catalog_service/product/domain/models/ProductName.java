package com.ecommerce.catalog_service.product.domain.models;

import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;

import java.util.regex.Pattern;

// Value Object para encapsular las reglas que debe tener el nombre
public record ProductName(String value) {
    // Lista blanca: Alfanuméricos, espacios, guiones, apóstrofes y caracteres acentuados (español).
    // Rechaza explícitamente caracteres de control o tags HTML como < >.
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\-']+$");

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 100;

    public ProductName {
        value = sanitizeAndValidate(value);
    }

    private static String sanitizeAndValidate(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            throw new BusinessRuleViolationException("El nombre del producto no puede ser nulo o vacío.");
        }

        // Reduccion de espacios multiples a un solo espacio
        String sanitized = rawName.trim().replaceAll("\\s+", " ");

        // Validacion de longitud minima y maxima
        if (sanitized.length() < MIN_LENGTH || sanitized.length() > MAX_LENGTH) {
            throw new BusinessRuleViolationException(
                    String.format("El nombre del producto debe tener entre %d y %d caracteres.", MIN_LENGTH, MAX_LENGTH)
            );
        }

        // Validacion de caracteres
        if (!VALID_NAME_PATTERN.matcher(sanitized).matches()) {
            throw new BusinessRuleViolationException("El nombre del producto contiene caracteres no permitidos.");
        }

        return sanitized;
    }
}
