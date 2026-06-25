package com.ecommerce.user_service.infra.adapters.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_addresses")
public class AddressJpaEntity {

    @Id
    private String id; // Usaremos el UUID del Dominio

    private String street;
    private String number;
    private String zipCode;
    private String city;
    private String state;
    private boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user; // Relación con el padre

    public AddressJpaEntity() {
    }

    public AddressJpaEntity(String id, String street, String number, String zipCode, String city, String state, boolean isDefault, UserJpaEntity user) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.isDefault = isDefault;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
