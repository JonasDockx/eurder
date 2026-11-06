package com.jonas.eurder.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String customerId;

    protected User() {
    }

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
