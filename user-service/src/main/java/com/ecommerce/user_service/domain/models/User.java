package com.ecommerce.user_service.domain.models;

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

    public User(String username, String email, String passwordHash) {
        validateRegistrationData(username, email, passwordHash);

        this.username = username.trim();
        this.email = email.trim().toLowerCase(); // Guardamos el email siempre en minúsculas
        this.passwordHash = passwordHash.trim();
        this.role = Role.USER;
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
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("El hash de la contraseña es obligatorio");
        }
    }

    public void completeProfile(PersonalInfo info, TaxStatus taxStatus) {
        if (info == null) throw new IllegalArgumentException("La información personal no puede ser nula");
        if (taxStatus == null) throw new IllegalArgumentException("El estado fiscal no puede ser nulo");

        this.personalInfo = info;
        this.taxStatus = taxStatus;
    }

    public void addAddress(Address address) {
        if (address == null) throw new IllegalArgumentException("La dirección no puede ser nula");

        if (this.addresses.isEmpty()) {
            address.setDefault(true);
        }
        this.addresses.add(address);
    }

    public void addPaymentMethod(SavedPaymentMethod paymentMethod) {
        if (paymentMethod == null) throw new IllegalArgumentException("El método de pago no puede ser nulo");

        if (this.paymentMethods.size() >= 5) {
            throw new IllegalStateException("Límite de tarjetas guardadas excedido");
        }
        this.paymentMethods.add(paymentMethod);
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
