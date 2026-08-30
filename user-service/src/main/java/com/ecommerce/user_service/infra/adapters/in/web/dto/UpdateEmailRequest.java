package com.ecommerce.user_service.infra.adapters.in.web.dto;

import java.util.UUID;

public record UpdateEmailRequest(
        String newEmail
) {
}
