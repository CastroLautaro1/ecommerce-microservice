package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.domain.models.User;

import java.util.UUID;

public interface UserQueryUseCases {
    User getProfile(UUID userId);
    boolean existsAndIsActive(UUID userId);
}
