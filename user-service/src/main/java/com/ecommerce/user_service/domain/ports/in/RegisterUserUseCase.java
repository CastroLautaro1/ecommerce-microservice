package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.application.commands.RegisterUserCommand;

import java.util.UUID;

public interface RegisterUserUseCase {
    UUID execute(RegisterUserCommand command);
}
