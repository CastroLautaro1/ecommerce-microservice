package com.ecommerce.user_service.infra.adapters.in.web;

import com.ecommerce.user_service.application.commands.*;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.in.*;
import com.ecommerce.user_service.infra.adapters.in.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final UpdateEmailUseCase updateEmailUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeactivateAccountUseCase deactivateAccountUseCase;
    private final UserQueryUseCases userQueryUseCases;

    public UserController(RegisterUserUseCase registerUserUseCase,
                          LoginUserUseCase loginUserUseCase,
                          UpdatePasswordUseCase updatePasswordUseCase,
                          UpdateEmailUseCase updateEmailUseCase,
                          UpdateProfileUseCase updateProfileUseCase,
                          DeactivateAccountUseCase deactivateAccountUseCase,
                          UserQueryUseCases userQueryUseCases) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.updatePasswordUseCase = updatePasswordUseCase;
        this.updateEmailUseCase = updateEmailUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.deactivateAccountUseCase = deactivateAccountUseCase;
        this.userQueryUseCases = userQueryUseCases;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );
        Long newUserId = registerUserUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponse(newUserId, request.firstName(), request.lastName(), request.email()));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        LoginUserCommand command = new LoginUserCommand(request.email(), request.password());

        User loggedInUser = loginUserUseCase.execute(command);

        // En el futuro se tendria que devolver el token jwt, de momento solo devuelvo los datos del usuario
        return ResponseEntity.ok(new UserResponse(
                loggedInUser.getId(),
                loggedInUser.getPersonalInfo().firstName(),
                loggedInUser.getPersonalInfo().lastName(),
                loggedInUser.getEmail()
        ));
    }

    // Usamos "/me" y capturamos la cabecera para prevenir el IDOR
    @PutMapping("/me/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestHeader("X-User-Id") Long authenticatedUserId,
            @RequestBody UpdateProfileRequest request) {

        UpdateProfileCommand command = new UpdateProfileCommand(
                authenticatedUserId,
                request.personalInfo(),
                request.taxStatus()
        );

        updateProfileUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordRequest request) {
        UpdatePasswordCommand command = new UpdatePasswordCommand(
                request.userId(),
                request.currentPassword(),
                request.newPassword()
        );
        updatePasswordUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/email")
    public ResponseEntity<Void> updateEmail(@RequestBody UpdateEmailRequest request) {
        UpdateEmailCommand command = new UpdateEmailCommand(
                request.userId(),
                request.newEmail()
        );
        updateEmailUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateAccount(
            @RequestHeader("X-User-Id") Long authenticatedUserId) {

        deactivateAccountUseCase.execute(authenticatedUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@RequestHeader("X-User-Id") Long authenticatedUserId) {
        User user = userQueryUseCases.getProfile(authenticatedUserId);

        // Mapeamos el modelo de dominio al DTO de respuesta
        return ResponseEntity.ok(UserProfileResponse.fromDomain(user));
    }
}
