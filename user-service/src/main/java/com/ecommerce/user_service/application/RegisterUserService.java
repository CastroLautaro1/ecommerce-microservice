package com.ecommerce.user_service.application;

import com.ecommerce.user_service.domain.exceptions.DomainValidationException;
import com.ecommerce.user_service.domain.exceptions.UserAlreadyExistsException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.RegisterUserCommand;
import com.ecommerce.user_service.domain.ports.in.RegisterUserUseCase;
import com.ecommerce.user_service.domain.ports.out.PasswordEncoderPort;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        String generatedUsername = generateUniqueUsername(command.firstName(), command.lastName());

        String hashedPassword = passwordEncoder.encode(command.rawPassword());

        User newUser = new User(generatedUsername, command.email(), hashedPassword, command.firstName(), command.lastName());

        User savedUser = userRepository.save(newUser);

        return savedUser.getId();
    }

    // Método privado para la generación del username
    private String generateUniqueUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new DomainValidationException("El nombre y apellido son obligatorios para el registro");
        }

        // Reemplazamos cualquier tipo de simbolo
        String base = (firstName + lastName)
                .toLowerCase()
                .replaceAll("[^a-z]", "");

        // Fallback en caso de que el nombre solamente tenga simbolos
        if (base.isBlank()) {
            base = "user";
        }

        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6);

        return base + uniqueSuffix; // Ej de username: "lautarocastro4f9d1a"
    }
}
