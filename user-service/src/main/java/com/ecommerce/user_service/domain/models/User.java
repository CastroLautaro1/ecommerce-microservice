package com.ecommerce.user_service.domain.models;

import com.ecommerce.user_service.domain.exceptions.AccountDeactivatedException;
import com.ecommerce.user_service.domain.exceptions.DomainValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private PersonalInfo personalInfo;
    private TaxStatus taxStatus;
    private List<Address> addresses = new ArrayList<>(); // Se pueden tener varias direcciones
    private List<SavedPaymentMethod> paymentMethods = new ArrayList<>(); // Se pueden tener varias tarjetas
    private boolean active;
    private LocalDateTime createdAt;

    // Constructor usado para el Registro
    public User(String username, String email, String passwordHash, String firstName, String lastName) {
        validateRegistrationData(username, email, passwordHash);

        this.username = username.trim();
        this.email = email.trim().toLowerCase(); // Guardamos el email siempre en minúsculas
        this.passwordHash = passwordHash.trim();
        this.role = Role.USER;
        this.personalInfo = new PersonalInfo(firstName, lastName, null, null);
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(Long id, String username, String email, String passwordHash, Role role,
                PersonalInfo personalInfo, TaxStatus taxStatus, List<Address> addresses,
                List<SavedPaymentMethod> paymentMethods, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.personalInfo = personalInfo;
        this.taxStatus = taxStatus;
        if (addresses != null) this.addresses = addresses;
        if (paymentMethods != null) this.paymentMethods = paymentMethods;
        this.active = active;
        this.createdAt = createdAt;
    }

    // --- REGLAS DE NEGOCIO (COMPORTAMIENTO) ---

    private void validateRegistrationData(String username, String email, String passwordHash) {
        if (username == null || username.isBlank() || username.length() < 3 || username.length() > 30) {
            throw new DomainValidationException("El nombre de usuario es obligatorio y debe tener entre 3 y 30 caracteres");
        }

        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new DomainValidationException("El formato del correo electrónico es inválido");
        }
        this.email = email.toLowerCase().trim();

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainValidationException("El hash de la contraseña es obligatorio");
        }
    }

    public void updateProfile(PersonalInfo info, TaxStatus taxStatus) {
        ensureAccountIsActive();
        if (info == null) {
            throw new DomainValidationException("La información personal no puede ser nula");
        }

        // Verificamos si el usuario YA tenía un DNI registrado en la base de datos
        if (this.personalInfo != null && this.personalInfo.isDocumentNumberComplete()) {
            String existingDni = this.personalInfo.documentNumber();
            String incomingDni = info.documentNumber();

            // Si el cliente intenta enviar un DNI nuevo y diferente al que ya tenia se arroja una excepcion
            if (incomingDni != null && !incomingDni.isBlank() && !existingDni.equals(incomingDni)) {
                throw new DomainValidationException("El número de documento no puede ser modificado una vez registrado.");
            }

            if (incomingDni == null || incomingDni.isBlank()) {
                info = new PersonalInfo(
                        info.firstName(),
                        info.lastName(),
                        existingDni, // Rescatamos el DNI inmutable
                        info.phoneNumber()
                );
            }
        }

        this.personalInfo = info;
        this.taxStatus = taxStatus != null ? taxStatus : this.taxStatus;
    }

    public void addAddress(Address address) {
        ensureAccountIsActive();
        if (address == null) throw new DomainValidationException("La dirección no puede ser nula");
        if (this.addresses.size() >= 8) {
            throw new DomainValidationException("Límite de direcciones de envío excedido (Máximo 8)");
        }

        // Si la nueva es default, desmarcamos las demás
        if (address.isDefault() || this.addresses.isEmpty()) {
            address.setDefault(true);
            this.addresses.forEach(a -> a.setDefault(false));
        }

        this.addresses.add(address);
    }

    public void addPaymentMethod(SavedPaymentMethod paymentMethod) {
        ensureAccountIsActive();
        if (paymentMethod == null) throw new DomainValidationException("El método de pago no puede ser nulo");
        if (this.paymentMethods.size() >= 5) {
            throw new DomainValidationException("Límite de tarjetas guardadas excedido (Máximo 5)");
        }

        // Si la nueva es default, desmarcamos las demás
        if (paymentMethod.isDefault() || this.paymentMethods.isEmpty()) {
            paymentMethod.setDefault(true);
            this.paymentMethods.forEach(p -> p.setDefault(false));
        }

        this.paymentMethods.add(paymentMethod);
    }

    public void deactivate() {
        if (!this.active) {
            throw new AccountDeactivatedException("El usuario ya se encuentra desactivado");
        }
        this.active = false;
    }

    // Método auxiliar privado
    private void ensureAccountIsActive() {
        if (!this.active) {
            throw new AccountDeactivatedException("La operación no puede realizarse porque la cuenta está desactivada");
        }
    }

    // GETTERS
    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public String getPasswordHash() { return passwordHash; }

    public Role getRole() { return role; }

    public PersonalInfo getPersonalInfo() { return personalInfo; }

    public TaxStatus getTaxStatus() { return taxStatus; }

    public List<Address> getAddresses() { return addresses; }

    public List<SavedPaymentMethod> getPaymentMethods() { return paymentMethods; }

    public boolean isActive() { return active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
