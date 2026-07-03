package com.ecommerce.user_service.application;

import com.ecommerce.user_service.domain.exceptions.UserAlreadyExistsException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.RegisterUserCommand;
import com.ecommerce.user_service.domain.ports.in.RegisterUserUseCase;
import com.ecommerce.user_service.domain.ports.out.PasswordEncoderPort;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUserService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Long execute(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException("Este correo electrónico ya se encuentra registrado. Por favor, inicia sesión.");
        }
        if (userRepository.existsByUsername(command.username())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya está en uso. Por favor, elige otro.");
        }

        String hashedPassword = passwordEncoder.encode(command.rawPassword());

        User newUser = new User(command.username(), command.email(), hashedPassword);

        User savedUser = userRepository.save(newUser);
        return savedUser.getId();
    }
}
