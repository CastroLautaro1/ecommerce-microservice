package com.ecommerce.user_service.application.services;

import com.ecommerce.user_service.domain.exceptions.UserNotFoundException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.UserQueryUseCases;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserQueryService implements UserQueryUseCases {

    private final UserRepositoryPort userRepository;

    public UserQueryService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getProfile(Long userId) {
        return userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public boolean existsAndIsActive(Long userId) {
        return userRepository.existsAndIsActive(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
