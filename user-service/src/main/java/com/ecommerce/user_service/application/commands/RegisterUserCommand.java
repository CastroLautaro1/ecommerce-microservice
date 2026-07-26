package com.ecommerce.user_service.application.commands;

public record RegisterUserCommand(
        String firstName,
        String lastName,
        String email,
        String rawPassword
) {
}
