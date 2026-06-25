package com.ecommerce.user_service.infra.adapters.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_payment_methods")
public class SavedPaymentMethodJpaEntity {

    @Id
    private String id;

    private String token;
    private String cardBrand;
    private String lastFourDigits;
    private boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user; // Relación con el padre

    public SavedPaymentMethodJpaEntity() {
    }

    public SavedPaymentMethodJpaEntity(String id, String token, String cardBrand, String lastFourDigits, boolean isDefault, UserJpaEntity user) {
        this.id = id;
        this.token = token;
        this.cardBrand = cardBrand;
        this.lastFourDigits = lastFourDigits;
        this.isDefault = isDefault;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }
}
