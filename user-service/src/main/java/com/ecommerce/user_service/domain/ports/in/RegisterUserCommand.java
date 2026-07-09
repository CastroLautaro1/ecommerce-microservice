package com.ecommerce.user_service.domain.ports.in;

public record RegisterUserCommand(
        String firstName,
        String lastName,
        String email,
        String rawPassword
) {
}
