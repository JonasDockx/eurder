package com.jonas.eurder.domain;

import jakarta.persistence.*;

import java.util.Objects;

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

    public Role getRole() {
        return role;
    }

    public User withEmail(String newEmail) {
        return new User(newEmail, password, role, customerId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(email, user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
