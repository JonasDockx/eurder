package com.jonas.eurder.repository;

import com.jonas.eurder.domain.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest {
    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 19);

    private final Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com", new Address("Test", "123", "1234", "Brussels"), "0123456789");
    private final Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
    private final Item pen = new Item("Pen", "A blue pen", new BigDecimal("1.50"), 10);
    private final List<ItemGroup> itemGroups = List.of(
            new ItemGroup(book, 2, ORDER_DATE),
            new ItemGroup(pen, 4, ORDER_DATE));
    private final Order order = new Order("customer-id", itemGroups);

    private final OrderRepository orderRepository = new OrderRepository();

    @Test
    void givenSavedOrder_whenGetById_thenShouldReturnTheSameOrder() {
        orderRepository.save(order);

        assertThat(orderRepository.getById(order.getId())).contains(order);
    }

}