package com.jonas.eurder.domain;

public class User {
    private final String email;
    private final String password;
    private final Role role;
    private final String customerId;

    public User(String email, String password, Role role, String customerId) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public String getCustomerId() {
        return customerId;
    }

    public boolean hasAccessTo(Feature feature) {
        return role.containsFeature(feature);
    }

    public boolean passwordMatches(String password) {
        return this.password.equals(password);
    }
}
