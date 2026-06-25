package com.ecommerce.user_service.infra.adapters.out.persistence.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class PersonalInfoEmbeddable {

    private String firstName;
    private String lastName;
    private String documentNumber;
    private String phoneNumber;

    public PersonalInfoEmbeddable() {
    }

    public PersonalInfoEmbeddable(String firstName, String lastName, String documentNumber, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
