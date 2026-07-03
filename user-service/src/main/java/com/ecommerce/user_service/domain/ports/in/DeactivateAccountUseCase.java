package com.ecommerce.user_service.domain.ports.in;

public interface DeactivateAccountUseCase {
    void execute(Long userId);
}
