package com.ecommerce.user_service.application.services;

import com.ecommerce.user_service.domain.exceptions.UserAlreadyExistsException;
import com.ecommerce.user_service.domain.exceptions.UserNotFoundException;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.application.commands.UpdateProfileCommand;
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

        String newDni = command.personalInfo().documentNumber();
        String newPhone = command.personalInfo().phoneNumber();

        // Validar que el DNI no esta tomado
        if (newDni != null && userRepository.existsByDocumentNumberAndIdNot(newDni, command.userId())) {
            throw new UserAlreadyExistsException("El número de documento ya se encuentra registrado por otro usuario");
        }

        // Validar que el celular no este tomado
        if (newPhone != null && userRepository.existsByPhoneNumberAndIdNot(newPhone, command.userId())) {
            throw new UserAlreadyExistsException("El número de teléfono ya se encuentra registrado por otro usuario");
        }

        user.updateProfile(command.personalInfo(), command.taxStatus());

        userRepository.save(user);
    }
}
