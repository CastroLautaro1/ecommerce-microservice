package com.ecommerce.user_service.domain.ports.in;

import java.util.UUID;

public interface DeactivateAccountUseCase {
    void execute(UUID userId);
}
