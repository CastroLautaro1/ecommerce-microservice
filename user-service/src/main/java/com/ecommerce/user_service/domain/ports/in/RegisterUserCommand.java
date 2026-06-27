package com.ecommerce.user_service.domain.ports.in;

public record RegisterUserCommand(
        String username,
        String email,
        String rawPassword
) {
}
