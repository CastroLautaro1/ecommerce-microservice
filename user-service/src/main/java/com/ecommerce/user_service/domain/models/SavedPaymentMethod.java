package com.ecommerce.user_service.domain.models;

public class SavedPaymentMethod {
    private String id;
    private String token; // El token seguro del procesador de pagos
    private String cardBrand; // "VISA", "MASTERCARD"
    private String lastFourDigits; // "4242"
    private boolean isDefault;

    public SavedPaymentMethod(String token, String cardBrand, String lastFourDigits, boolean isDefault) {
        this.token = token;
        this.cardBrand = cardBrand;
        this.lastFourDigits = lastFourDigits;
        this.isDefault = isDefault;
    }

    public SavedPaymentMethod(String id, String token, String cardBrand, String lastFourDigits, boolean isDefault) {
        this.id = id;
        this.token = token;
        this.cardBrand = cardBrand;
        this.lastFourDigits = lastFourDigits;
        this.isDefault = isDefault;
    }

    // --- Agregar validacionaes de dominio


    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
