package com.ecommerce.user_service.application.commands;

import java.util.UUID;

public record UpdatePasswordCommand(
        UUID userId,
        String currentPassword,
        String newPassword
) {
    // Aca podria agregar validaciones de caracteres
}
