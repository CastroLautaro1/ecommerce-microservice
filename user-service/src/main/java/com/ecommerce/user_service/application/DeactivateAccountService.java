package com.ecommerce.user_service.application;

import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.DeactivateAccountUseCase;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import jakarta.transaction.Transactional;

public class DeactivateAccountService implements DeactivateAccountUseCase {

    private final UserRepositoryPort userRepository;

    public DeactivateAccountService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.deactivate();

        userRepository.save(user);
    }
}
