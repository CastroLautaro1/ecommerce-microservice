package com.ecommerce.user_service.infra.adapters.in.web.dto;

public record UpdatePasswordRequest(
        Long userId,
        String currentPassword,
        String newPassword
) {
}
