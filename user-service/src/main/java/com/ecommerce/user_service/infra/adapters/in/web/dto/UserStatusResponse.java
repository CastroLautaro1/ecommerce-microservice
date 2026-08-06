package com.ecommerce.user_service.infra.adapters.in.web.dto;

public record UserStatusResponse(
        Long userId,
        boolean isActive
) {
}
