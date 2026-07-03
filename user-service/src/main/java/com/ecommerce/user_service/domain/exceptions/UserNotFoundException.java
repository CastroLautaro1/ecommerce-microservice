package com.ecommerce.user_service.domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("No se encontró ningún usuario con el ID: " + id);
    }

    public UserNotFoundException(String email) {
        super("No se encontró ningún usuario con el email: " + email);
    }
}
