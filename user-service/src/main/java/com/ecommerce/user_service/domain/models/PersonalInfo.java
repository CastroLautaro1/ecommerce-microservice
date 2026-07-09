package com.ecommerce.user_service.domain.models;

import com.ecommerce.user_service.domain.exceptions.DomainValidationException;

public record PersonalInfo(
        String firstName, // Se carga en el Registo
        String lastName, // Se carga en el Registo
        String documentNumber, // Puede ser null y se carga despues
        String phoneNumber // Puede ser null y se carga despues
) {
    public PersonalInfo {
        firstName = firstName != null ? firstName.trim() : null;
        lastName = lastName != null ? lastName.trim() : null;
        documentNumber = documentNumber != null ? documentNumber.trim() : null;
        phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;

        validateLength(firstName, 2, 50, "El nombre");
        validateLength(lastName, 2, 50, "El apellido");

        // El documento es opcional, pero si lo envian debe ser valido
        if (documentNumber != null && !documentNumber.isBlank()) {
            validateLength(documentNumber, 7, 8, "El número de documento");
            if (!documentNumber.matches("\\d+")) {
                throw new DomainValidationException("El número de documento debe contener únicamente dígitos");
            }
        }

        // El telefono es opcional, pero si lo envian debe ser valido
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            validateLength(phoneNumber, 8, 15, "El número de teléfono");
            if (!phoneNumber.matches("^[+]*[0-9]{8,15}$")) {
                throw new DomainValidationException("El formato del número de teléfono es inválido");
            }
        }
    }

    // Método de negocio para saber si el numero de coumento ya fue cargado
    public boolean isDocumentNumberComplete() {
        return documentNumber != null && !documentNumber.isBlank();
    }

    // Método privado auxiliar para validar la longitud
    private static void validateLength(String value, int min, int max, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(fieldName + " es obligatorio");
        }
        if (value.length() < min || value.length() > max) {
            throw new DomainValidationException(
                    String.format("%s debe tener entre %d y %d caracteres", fieldName, min, max)
            );
        }
    }
}
