package com.ecommerce.user_service.infra.adapters.in.web.dto;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
