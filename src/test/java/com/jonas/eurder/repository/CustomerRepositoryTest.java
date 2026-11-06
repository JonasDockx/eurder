package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Address;
import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.service.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

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
    void givenSavedCustomer_whenGetById_thenShouldReturnTheSameCustomer() {
        customerRepository.save(customer);
        entityManager.flush();
        entityManager.clear();

        Customer found = customerRepository.findById(customer.getId()).orElseThrow();

        assertThat(found).isEqualTo(customer);
    }

    @Test
    void givenNoCustomers_whenGetAll_thenReturnsEmptyList() {
        assertThat(customerRepository.findAll()).isEmpty();
    }

    @Test
    void givenSavedCustomers_whenGetALL_thenReturnsAllOfThem() {
        Customer otherCustomer = new Customer("Jos", "De Vos", "jos@test.com", address, "0987654321");
        customerRepository.save(customer);
        customerRepository.save(otherCustomer);

        assertThat(customerRepository.findAll()).containsExactlyInAnyOrder(customer, otherCustomer);
    }

    @Test
    void getAll_returnsACopyThatCannotChangeTheRepository() {
        customerRepository.save(customer);

        assertThatThrownBy(() -> customerRepository.findAll().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(customerRepository.findAll()).hasSize(1);
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
                .isInstanceOf(EmailAlreadyExistsException.class);
    }
}