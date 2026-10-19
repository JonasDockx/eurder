package com.jonas.eurder.domain;

import java.util.UUID;

public class Customer {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Address address;
    private final String phoneNumber;

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

    private static String validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must be provided");
        }
        return value;
    }

    private static <T> T validateNotNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must be provided");
        }
        return value;
    }
}
