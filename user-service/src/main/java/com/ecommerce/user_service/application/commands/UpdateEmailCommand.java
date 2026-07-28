package com.ecommerce.user_service.application.commands;

public record UpdateEmailCommand(
        Long userId,
        String newEmail
) {
}
