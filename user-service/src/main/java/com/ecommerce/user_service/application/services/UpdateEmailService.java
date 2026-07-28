package com.ecommerce.user_service.application.services;

import com.ecommerce.user_service.application.commands.UpdateEmailCommand;
import com.ecommerce.user_service.domain.exceptions.UserAlreadyExistsException;
import com.ecommerce.user_service.domain.exceptions.UserNotFoundException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.UpdateEmailUseCase;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateEmailService implements UpdateEmailUseCase {

    private final UserRepositoryPort userRepository;

    public UpdateEmailService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(UpdateEmailCommand command) {
        if (userRepository.existsByEmail(command.newEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya se encuentra en uso.");
        }

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        user.updateEmail(command.newEmail());

        userRepository.save(user);
    }
}
