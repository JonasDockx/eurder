package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Address;
import com.jonas.eurder.domain.Customer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryTest {

    private final Address address = new Address("Test Street", "1", "1000", "Brussels");
    private final Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com", address, "0123456789");

    private final CustomerRepository customerRepository = new CustomerRepository();

    @Test
    void givenSavedCustomer_whenGetById_thenShouldReturnTheSameCustomer() {
        customerRepository.save(customer);

        assertThat(customerRepository.getById(customer.getId())).contains(customer);
    }
}