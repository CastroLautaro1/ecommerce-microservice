package com.ecommerce.user_service.infra.adapters.in.web.dto;

public record LoginRequest(
        String email,
        String password
) {
}
