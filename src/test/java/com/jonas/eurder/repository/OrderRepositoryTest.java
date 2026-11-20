package com.jonas.eurder.repository;

import com.jonas.eurder.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class OrderRepositoryTest {

    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 20);
    private static final Address ADDRESS = new Address("Test Street", "1", "1000", "Brussels", "Belgium");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer jonas;
    private Customer bram;
    private Item book;
    private Item pen;

    @BeforeEach
    void setUp() {
        jonas = customerRepository.save(new Customer("Jonas", "Dockx", "jonas@test.com", ADDRESS, "0123456789"));
        bram = customerRepository.save(new Customer("Bram", "Test", "bram@test.com", ADDRESS, "0987654321"));
        book = itemRepository.save(new Item("Book A", "A long book", new BigDecimal("14.99"), 5));
        pen = itemRepository.save(new Item("Pen", "A blue pen", new BigDecimal("1.50"), 10));
    }

    @Test
    void givenSavedOrder_whenFindById_thenReturnsEqualCopyWithAllFields() {
        Order order = new Order(jonas.getId(), List.of(
                new ItemGroup(book, 2, ORDER_DATE),
                new ItemGroup(pen, 4, ORDER_DATE)
        ));
        orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        Order found = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(found).isNotSameAs(order);
        assertThat(found).isEqualTo(order).hasSameHashCodeAs(order);

        assertThat(found.getCustomerId()).isEqualTo(jonas.getId());
        assertThat(found.getItemGroups())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(order.getItemGroups());
        assertThat(found.getTotalPrice()).isEqualByComparingTo("35.98");
    }


    @Test
    void findByCustomerId_returnsOnlyThatCustomersOrders() {
        Order ordersOfJonas1 = orderRepository.save(new Order(jonas.getId(), List.of(new ItemGroup(book, 1, ORDER_DATE))));
        Order ordersOfJonas2 = orderRepository.save(new Order(jonas.getId(), List.of(new ItemGroup(book, 2, ORDER_DATE))));
        orderRepository.save(new Order(bram.getId(), List.of(new ItemGroup(book, 3, ORDER_DATE))));

        assertThat(orderRepository.findByCustomerId(jonas.getId()))
                .containsExactlyInAnyOrder(ordersOfJonas1, ordersOfJonas2);
    }

    @Test
    void findByCustomerId_givenCustomerWithoutOrders_returnsEmptyList() {
        orderRepository.save(new Order(bram.getId(), List.of(new ItemGroup(book, 1, ORDER_DATE))));

        assertThat(orderRepository.findByCustomerId(jonas.getId())).isEmpty();
    }

    @Test
    void givenOrderForUnknownCustomer_whenSaving_thenDatabaseRefusesIt() {
        Order order = new Order("unknown-customer", List.of(new ItemGroup(book, 1, ORDER_DATE)));

        assertThatThrownBy(() -> orderRepository.saveAndFlush(order))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}