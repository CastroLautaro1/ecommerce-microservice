package com.ecommerce.user_service.infra.adapters.in.web.dto;

import java.util.UUID;

public record UpdatePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
