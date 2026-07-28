package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.application.commands.RegisterUserCommand;

public interface RegisterUserUseCase {
    Long execute(RegisterUserCommand command);
}
