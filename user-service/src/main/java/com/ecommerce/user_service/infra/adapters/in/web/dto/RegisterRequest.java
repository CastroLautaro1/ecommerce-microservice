package com.ecommerce.user_service.infra.adapters.in.web.dto;

public record RegisterRequest(
        String username,
        String email,
        String password
) {
}
