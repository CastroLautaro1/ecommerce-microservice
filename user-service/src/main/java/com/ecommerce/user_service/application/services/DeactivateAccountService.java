package com.ecommerce.user_service.application.services;

import com.ecommerce.user_service.domain.exceptions.UserNotFoundException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.DeactivateAccountUseCase;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeactivateAccountService implements DeactivateAccountUseCase {

    private final UserRepositoryPort userRepository;

    public DeactivateAccountService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        user.deactivate();

        userRepository.save(user);
    }
}
