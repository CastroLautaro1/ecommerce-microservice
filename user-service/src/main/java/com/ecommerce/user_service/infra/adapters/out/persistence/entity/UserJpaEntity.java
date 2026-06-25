package com.ecommerce.user_service.infra.adapters.out.persistence.entity;

import com.ecommerce.user_service.domain.models.Role;
import com.ecommerce.user_service.domain.models.TaxStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Embedded
    private PersonalInfoEmbeddable personalInfo;

    @Enumerated(EnumType.STRING)
    private TaxStatus taxStatus;

    // Si el usuario se elimina, sus direcciones y tarjetas también.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressJpaEntity> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedPaymentMethodJpaEntity> paymentMethods = new ArrayList<>();

    private boolean active;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // --- Métodos de sincronización bidireccional ---

    public void addAddress(AddressJpaEntity address) {
        addresses.add(address);
        address.setUser(this);
    }

    public void addPaymentMethod(SavedPaymentMethodJpaEntity paymentMethod) {
        paymentMethods.add(paymentMethod);
        paymentMethod.setUser(this);
    }

    public UserJpaEntity() {
    }

    public UserJpaEntity(Long id, String username, String email, String passwordHash, Role role, PersonalInfoEmbeddable personalInfo, TaxStatus taxStatus, List<AddressJpaEntity> addresses, List<SavedPaymentMethodJpaEntity> paymentMethods, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.personalInfo = personalInfo;
        this.taxStatus = taxStatus;
        this.addresses = addresses;
        this.paymentMethods = paymentMethods;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PersonalInfoEmbeddable getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfoEmbeddable personalInfo) {
        this.personalInfo = personalInfo;
    }

    public TaxStatus getTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(TaxStatus taxStatus) {
        this.taxStatus = taxStatus;
    }

    public List<AddressJpaEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressJpaEntity> addresses) {
        this.addresses = addresses;
    }

    public List<SavedPaymentMethodJpaEntity> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<SavedPaymentMethodJpaEntity> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
