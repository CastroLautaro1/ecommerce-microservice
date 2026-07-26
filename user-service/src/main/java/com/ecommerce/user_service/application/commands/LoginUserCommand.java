package com.ecommerce.user_service.application.commands;

public record LoginUserCommand(
        String email,
        String rawPassword
) {
}
