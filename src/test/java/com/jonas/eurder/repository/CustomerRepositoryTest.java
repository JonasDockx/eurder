package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Address;
import com.jonas.eurder.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final Address address = new Address("Test Street", "1", "1000", "Brussels");
    private final Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com", address, "0123456789");

    @Test
    void givenSavedCustomer_whenFindById_thenShouldReturnTheSameCustomer() {
        customerRepository.save(customer);
        entityManager.flush();
        entityManager.clear();

        Customer found = customerRepository.findById(customer.getId()).orElseThrow();

        assertThat(found).usingRecursiveComparison().isEqualTo(customer);
    }

    @Test
    void givenNoCustomers_whenFindAll_thenReturnsEmptyList() {
        assertThat(customerRepository.findAll()).isEmpty();
    }

    @Test
    void givenSavedCustomers_whenFindAll_thenReturnsAllOfThem() {
        Customer otherCustomer = new Customer("Jos", "De Vos", "jos@test.com", address, "0987654321");
        customerRepository.save(customer);
        customerRepository.save(otherCustomer);

        assertThat(customerRepository.findAll()).containsExactlyInAnyOrder(customer, otherCustomer);
    }

    @Test
    void givenNoCustomers_whenFindById_thenReturnsEmptyOptional() {
        assertThat(customerRepository.findById("123")).isEmpty();
    }

    @Test
    void givenExistingCustomer_whenExistsById_thenReturnTrue() {
        customerRepository.save(customer);

        assertThat(customerRepository.existsById(customer.getId())).isTrue();
    }

    @Test
    void givenNonExistingCustomer_whenExistsById_thenReturnFalse() {
        assertThat(customerRepository.existsById("NonExistingId")).isFalse();
    }

    @Test
    void givenCustomerWithSameEmail_whenSaving_thenDatabaseRefusesIt() {
        customerRepository.saveAndFlush(customer);
        Customer duplicate = new Customer("Other", "Person", "jonas@test.com", address, "0111111111");

        assertThatThrownBy(() -> customerRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}