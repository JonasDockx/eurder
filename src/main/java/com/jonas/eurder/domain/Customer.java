package com.jonas.eurder.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

import static com.jonas.eurder.domain.Validation.validateNotBlank;
import static com.jonas.eurder.domain.Validation.validateNotNull;

@Entity
public class Customer {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    @Embedded
    private Address address;
    private String phoneNumber;

    protected Customer() {
    }

    public Customer(String firstName, String lastName, String email, Address address, String phoneNumber) {
        this.id = UUID.randomUUID().toString();
        this.firstName = validateNotBlank(firstName, "First name");
        this.lastName = validateNotBlank(lastName, "Last name");
        this.email = validateNotBlank(email, "E-mail");
        this.address = validateNotNull(address, "Address");
        this.phoneNumber = validateNotBlank(phoneNumber, "Phone number");
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
