package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.application.commands.LoginUserCommand;
import com.ecommerce.user_service.domain.models.User;

public interface LoginUserUseCase {
    User execute(LoginUserCommand command);
}
