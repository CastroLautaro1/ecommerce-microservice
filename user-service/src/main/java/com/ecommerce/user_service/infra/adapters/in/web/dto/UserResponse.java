package com.ecommerce.user_service.infra.adapters.in.web.dto;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
