package com.ecommerce.user_service.application;

import com.ecommerce.user_service.domain.exceptions.UserNotFoundException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.UpdateProfileCommand;
import com.ecommerce.user_service.domain.ports.in.UpdateProfileUseCase;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserRepositoryPort userRepository;

    public UpdateProfileService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(UpdateProfileCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        user.completeProfile(command.personalInfo(), command.taxStatus());

        userRepository.save(user);
    }
}
