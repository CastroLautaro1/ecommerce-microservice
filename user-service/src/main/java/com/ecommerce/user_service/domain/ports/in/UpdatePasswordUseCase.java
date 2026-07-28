package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.application.commands.UpdatePasswordCommand;

public interface UpdatePasswordUseCase {
    void execute(UpdatePasswordCommand command);
}
