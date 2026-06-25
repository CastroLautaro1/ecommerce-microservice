package com.ecommerce.user_service.domain.models;

public class Adress {
    private String id; // Generado por UUID
    private String street;
    private String number;
    private String zipCode;
    private String city;
    private String state;
    private boolean isDefault;

    public Adress(String street, String number, String zipCode, String city, String state, boolean isDefault) {
        this.street = street;
        this.number = number;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.isDefault = isDefault;
    }

    public Adress(String id, String street, String number, String zipCode, String city, String state, boolean isDefault) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.isDefault = isDefault;
    }

    // --- Agregar validaciones de dominio

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

    public void setStreet(String street) {
        this.street = street;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
