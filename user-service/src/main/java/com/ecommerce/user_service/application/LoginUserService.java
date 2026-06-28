package com.ecommerce.user_service.application;

import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.LoginUserCommand;
import com.ecommerce.user_service.domain.ports.in.LoginUserUseCase;
import com.ecommerce.user_service.domain.ports.out.PasswordEncoderPort;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!user.isActive()) {
            throw new IllegalStateException("La cuenta está desactivada. Contacte a soporte.");
        }

        if (!passwordEncoder.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return user;
    }
}
