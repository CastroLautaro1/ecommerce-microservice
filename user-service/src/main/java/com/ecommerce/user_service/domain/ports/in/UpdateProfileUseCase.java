package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.application.commands.UpdateProfileCommand;

public interface UpdateProfileUseCase {
    void execute(UpdateProfileCommand command);
}
