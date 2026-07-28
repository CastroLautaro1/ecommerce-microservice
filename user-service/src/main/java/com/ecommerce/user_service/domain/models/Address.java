package com.ecommerce.user_service.domain.models;

import java.util.UUID;

public class Address {
    private String id; // Generado por UUID
    private String street;
    private String number;
    private String zipCode;
    private String city;
    private String state;
    private boolean isDefault;

    public static Address registerAddress(String street, String number, String zipCode, String city, String state) {
        validate(street, number, zipCode, city, state);

        return new Address(
                null,
                street,
                number,
                zipCode,
                city,
                state,
                false// False por defecto, el agregado User decide si la hace True
        );
    }

    public Address(String id, String street, String number, String zipCode, String city, String state, boolean isDefault) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.isDefault = isDefault;
    }

    private static void validate(String street, String number, String zipCode, String city, String state) {
        if (street == null || street.isBlank()) throw new IllegalArgumentException("La calle es obligatoria");
        if (number == null || number.isBlank()) throw new IllegalArgumentException("El número o altura es obligatorio");
        if (zipCode == null || zipCode.isBlank()) throw new IllegalArgumentException("El código postal es obligatorio");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("La ciudad es obligatoria");
        if (state == null || state.isBlank()) throw new IllegalArgumentException("La provincia/estado es obligatoria");
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    // GETTERS

    public String getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
