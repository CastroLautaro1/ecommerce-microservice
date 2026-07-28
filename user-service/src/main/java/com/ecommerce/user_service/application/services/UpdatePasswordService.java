package com.ecommerce.user_service.application.services;

import com.ecommerce.user_service.application.commands.UpdatePasswordCommand;
import com.ecommerce.user_service.domain.exceptions.BusinessRuleViolationException;
import com.ecommerce.user_service.domain.exceptions.UserNotFoundException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.UpdatePasswordUseCase;
import com.ecommerce.user_service.domain.ports.out.PasswordEncoderPort;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UpdatePasswordService implements UpdatePasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public UpdatePasswordService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void execute(UpdatePasswordCommand command) {
        User.validateRawPasswordComplexity(command.newPassword()); // Valida texto plano de la contraseña

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleViolationException("La contraseña actual es incorrecta.");
        }
        if (passwordEncoder.matches(command.newPassword(), user.getPasswordHash())) {
            throw new BusinessRuleViolationException("La nueva contraseña no puede ser igual a la anterior.");
        }

        String encodedNewPassword = passwordEncoder.encode(command.newPassword());
        user.updatePassword(encodedNewPassword);

        userRepository.save(user);
    }
}
