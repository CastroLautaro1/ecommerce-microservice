package com.ecommerce.user_service.domain.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciales inválidas. Verifique su email y contraseña.");
    }
}
