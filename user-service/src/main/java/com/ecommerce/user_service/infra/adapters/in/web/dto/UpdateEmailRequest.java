package com.ecommerce.user_service.infra.adapters.in.web.dto;

public record UpdateEmailRequest(
        Long userId,
        String newEmail
) {
}
