package com.jonas.eurder.repository;

import com.jonas.eurder.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void adminUser_isCreatedByMigration() {
        User admin = userRepository.findByEmail("admin@eurder.com").orElseThrow();

        assertThat(admin.passwordMatches("admin")).isTrue();
        assertThat(admin.hasAccessTo(Feature.ADD_ITEM)).isTrue();
        assertThat(admin.getCustomerId()).isNull();
    }

    @Test
    void givenSavedCustomerUser_whenFindById_thenReturnsEqualCopyWithRoleAndCustomer() {
        Customer customer = customerRepository.save(
                new Customer("Jonas", "Dockx", "jonas@test.com",
                        new Address("Test street", "1", "1000", "Brussels"), "0123456789")
        );
        User user = new User("jonas@test.com", "secret", Role.CUSTOMER, customer.getId());

        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        User found = userRepository.findByEmail("jonas@test.com").orElseThrow();

        assertThat(found).isNotSameAs(user);
        assertThat(found).isEqualTo(user).hasSameHashCodeAs(user);
        assertThat(found.getCustomerId()).isEqualTo(customer.getId());
        assertThat(found.passwordMatches("secret")).isTrue();
        assertThat(found.hasAccessTo(Feature.ORDER_ITEM)).isTrue();
        assertThat(found.hasAccessTo(Feature.ADD_ITEM)).isFalse();

    }
}