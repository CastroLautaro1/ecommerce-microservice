package com.ecommerce.user_service.domain.models;

import java.util.UUID;

public class SavedPaymentMethod {
    private String id;
    private String token; // El token seguro del procesador de pagos
    private String cardBrand; // "VISA", "MASTERCARD"
    private String lastFourDigits; // "4242"
    private boolean isDefault;

    public static SavedPaymentMethod registerPaymentMethod(String token, String cardBrand, String lastFourDigits) {
        validate(token, cardBrand, lastFourDigits);

        return new SavedPaymentMethod(
                null,
                token,
                cardBrand,
                lastFourDigits,
                false
        );
    }

    public SavedPaymentMethod(String id, String token, String cardBrand, String lastFourDigits, boolean isDefault) {
        this.id = id;
        this.token = token;
        this.cardBrand = cardBrand;
        this.lastFourDigits = lastFourDigits;
        this.isDefault = isDefault;
    }

    private static void validate(String token, String cardBrand, String lastFourDigits) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token del procesador de pagos es obligatorio");
        }
        if (cardBrand == null || cardBrand.isBlank()) {
            throw new IllegalArgumentException("La marca de la tarjeta (cardBrand) es obligatoria");
        }
        // Deben ser exactamente 4 dígitos numéricos
        if (lastFourDigits == null || !lastFourDigits.matches("^\\d{4}$")) {
            throw new IllegalArgumentException("Deben ingresarse exactamente los últimos 4 dígitos numéricos de la tarjeta");
        }
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    // GETTERS

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
}
