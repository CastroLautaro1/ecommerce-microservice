package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.domain.models.User;

public interface UserQueryUseCases {
    User getProfile(Long userId);
}
