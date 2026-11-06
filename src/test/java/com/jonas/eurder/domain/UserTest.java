package com.jonas.eurder.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User createUser(String email) {
        return new User(email, "password", Role.CUSTOMER, "customerId");
    }

    @Test
    void equals_givenSameInstance_thenTrue() {
        User user = createUser("jonas@test.com");

        assertThat(user.equals(user)).isTrue();
    }

    @Test
    void equals_givenOtherCustomer_thenFalse() {
        User jonas = createUser("jonas@test.com");
        User bram = createUser("bram@test.com");

        assertThat(jonas.equals(bram)).isFalse();
    }

    @Test
    void equals_givenOtherType_thenFalse() {
        User user = createUser("jonas@test.com");

        assertThat(user.equals("Not a user")).isFalse();
    }

    @Test
    void equals_givenNull_thenFalse() {
        User user = createUser("jonas@test.com");

        assertThat(user.equals(null)).isFalse();
    }
}