package com.jonas.eurder.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    private final Address address = new Address("Test Street", "1", "1000", "Brussels");

    private Customer createCustomer(String firstName, String email) {
        return new Customer(firstName, "Dockx", email, address, "0123456789");
    }

    @Test
    void createCustomer_givenValidData_thenIdIsGeneratedAndFieldsAreSet() {
        Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com", address, "0123456789");

        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo("Jonas");
        assertThat(customer.getEmail()).isEqualTo("jonas@test.com");
    }

    @ParameterizedTest(name = "email [{0}] is rejected")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void createCustomer_givenMissingEmail_thenThrowsIllegalArgumentException(String email) {
        assertThatThrownBy(() -> createCustomer("Jonas", email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("E-mail must be provided");
    }

    @ParameterizedTest(name = "first name [{0}] is rejected")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void createCustomer_givenMissingFirstName_thenThrowsIllegalArgumentException(String firstName) {
        assertThatThrownBy(() -> createCustomer(firstName, "jonas@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name must be provided");
    }

    @Test
    void createCustomer_givenNullAddress_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Customer("Jonas", "Dockx", "jonas@test.com", null, "0123456789")).
                isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Address must be provided");
    }

    @Test
    void equals_givenSameInstance_thenTrue() {
        Customer customer = createCustomer("Jonas", "jonas@test.com");

        assertThat(customer.equals(customer)).isTrue();
    }

    @Test
    void equals_givenOtherCustomer_thenFalse() {
        Customer jonas = createCustomer("Jonas", "jonas@test.com");
        Customer jos = createCustomer("Jos", "jos@test;com");

        assertThat(jonas.equals(jos)).isFalse();
    }

    @Test
    void equals_givenOtherType_thenFalse() {
        Customer customer = createCustomer("Jonas", "jonas@test.com");

        assertThat(customer.equals("Not a customer")).isFalse();
    }

    @Test
    void equals_givenNull_thenFalse() {
        Customer customer = createCustomer("Jonas", "jonas@test.com");

        assertThat(customer.equals(null)).isFalse();
    }

}