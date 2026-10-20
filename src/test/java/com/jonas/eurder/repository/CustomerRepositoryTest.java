package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Address;
import com.jonas.eurder.domain.Customer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerRepositoryTest {

    private final Address address = new Address("Test Street", "1", "1000", "Brussels");
    private final Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com", address, "0123456789");

    private final CustomerRepository customerRepository = new CustomerRepository();

    @Test
    void givenSavedCustomer_whenGetById_thenShouldReturnTheSameCustomer() {
        customerRepository.save(customer);

        assertThat(customerRepository.getById(customer.getId())).contains(customer);
    }

    @Test
    void givenNoCustomers_whenGetAll_thenReturnsEmptyList() {
        assertThat(customerRepository.getAll()).isEmpty();
    }

    @Test
    void givenSavedCustomers_whenGetALL_thenReturnsAllOfThem() {
        Customer otherCustomer = new Customer("Jos", "De Vos", "jos@test.com", address, "0987654321");
        customerRepository.save(customer);
        customerRepository.save(otherCustomer);

        assertThat(customerRepository.getAll()).containsExactlyInAnyOrder(customer, otherCustomer);
    }

    @Test
    void getAll_returnsACopyThatCannotChangeTheRepository() {
        customerRepository.save(customer);

        assertThatThrownBy(() -> customerRepository.getAll().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(customerRepository.getAll()).hasSize(1);
    }

    @Test
    void givenNoCustomers_whenGetById_thenReturnsEmptyOptional() {
        assertThat(customerRepository.getById("123")).isEmpty();
    }
}