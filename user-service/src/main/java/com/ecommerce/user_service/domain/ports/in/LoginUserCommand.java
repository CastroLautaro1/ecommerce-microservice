package com.ecommerce.user_service.domain.ports.in;

public record LoginUserCommand(
        String email,
        String rawPassword
) {
}
