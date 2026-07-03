package com.ecommerce.user_service.domain.ports.in;

public interface RegisterUserUseCase {
    Long execute(RegisterUserCommand command);
}
