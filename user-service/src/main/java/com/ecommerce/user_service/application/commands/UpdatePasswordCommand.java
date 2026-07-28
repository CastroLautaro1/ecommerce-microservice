package com.ecommerce.user_service.application.commands;

public record UpdatePasswordCommand(
        Long userId,
        String currentPassword,
        String newPassword
) {
    // Aca podria agregar validaciones de caracteres
}
