package com.ecommerce.user_service.domain.ports.out;

import com.ecommerce.user_service.domain.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    // Para Registro, Edición y Baja Lógica
    User save(User user);

    // Para Login (buscar por correo y comparar contraseña)
    Optional<User> findByEmail(String email);

    // Para Edición y Baja Lógica (buscar al usuario autenticado)
    Optional<User> findById(UUID id);
    Optional<User> findActiveById(UUID id);

    // Para Registro (validar que no haya duplicados)
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByDocumentNumberAndIdNot(String documentNumber, UUID userId);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID userId);

    // Para Busquedas
    Optional<Boolean> existsAndIsActive(UUID userId);
}
