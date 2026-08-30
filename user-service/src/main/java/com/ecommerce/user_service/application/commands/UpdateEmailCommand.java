package com.ecommerce.user_service.application.commands;

import java.util.UUID;

public record UpdateEmailCommand(
        UUID userId,
        String newEmail
) {
}
