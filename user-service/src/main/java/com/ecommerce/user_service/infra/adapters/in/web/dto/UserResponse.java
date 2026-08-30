package com.ecommerce.user_service.infra.adapters.in.web.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
}
