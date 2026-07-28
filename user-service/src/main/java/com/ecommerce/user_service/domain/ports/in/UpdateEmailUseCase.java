package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.application.commands.UpdateEmailCommand;

public interface UpdateEmailUseCase {
    void execute(UpdateEmailCommand command);
}
