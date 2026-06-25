package com.ecommerce.user_service.domain.models;

public record PersonalInfo(
        String firstName,
        String lastName,
        String documentNumber, // DNI
        String phoneNumber
) {
    public PersonalInfo {
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("El apellido es obligatorio");
        if (documentNumber == null || documentNumber.isBlank()) throw new IllegalArgumentException("El numero de documento es obligatorio");
    }
}
