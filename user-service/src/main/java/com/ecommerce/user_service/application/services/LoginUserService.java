package com.ecommerce.user_service.application.services;

import com.ecommerce.user_service.domain.exceptions.AccountDeactivatedException;
import com.ecommerce.user_service.domain.exceptions.InvalidCredentialsException;
import com.ecommerce.user_service.domain.exceptions.UserNotFoundException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.application.commands.LoginUserCommand;
import com.ecommerce.user_service.domain.ports.in.LoginUserUseCase;
import com.ecommerce.user_service.domain.ports.out.PasswordEncoderPort;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUserService implements LoginUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public LoginUserService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User execute(LoginUserCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new UserNotFoundException(command.email()));

        if (!user.isActive()) {
            throw new AccountDeactivatedException("La cuenta está desactivada. Contacte a soporte.");
        }

        if (!passwordEncoder.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
