package com.ecommerce.user_service.domain.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("No se encontró ningún usuario con el ID: " + id);
    }

    public UserNotFoundException(String email) {
        super("No se encontró ningún usuario con el email: " + email);
    }
}
