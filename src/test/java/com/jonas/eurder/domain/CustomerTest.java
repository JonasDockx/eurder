package com.jonas.eurder.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    private final Address address = new Address("Test Street", "1", "1000", "Brussels");

    @Test
    void createCustomer_givenValidData_thenIdIsGeneratedAndFieldsAreSet() {
        Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com", address, "0123456789");

        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo("Jonas");
        assertThat(customer.getEmail()).isEqualTo("jonas@test.com");
    }

    @Test
    void createCustomer_givenBlankEmail_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Customer("Jonas", "Dockx", " ", address, "0123456789")).
                isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createCustomer_givenNullAddress_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Customer("Jonas", "Dockx", "jonas@test.com", null, "0123456789")).
                isInstanceOf(IllegalArgumentException.class);
    }

}