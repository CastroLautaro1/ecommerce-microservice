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
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = Role.USER;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // --- REGLAS DE NEGOCIO (COMPORTAMIENTO) ---

    public void completeProfile(PersonalInfo info, TaxStatus taxStatus) {
        this.personalInfo = info;
        this.taxStatus = taxStatus;
    }

    public void addAddress(Address address) {
        if (this.addresses.isEmpty()) {
            address.setDefault(true);
        }
        this.addresses.add(address);
    }

    public void addPaymentMethod(SavedPaymentMethod paymentMethod) {
        if (this.paymentMethods.size() >= 5) {
            throw new IllegalStateException("Límite de tarjetas guardadas excedido");
        }
        this.paymentMethods.add(paymentMethod);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public TaxStatus getTaxStatus() {
        return taxStatus;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public List<SavedPaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
