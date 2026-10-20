package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Item;
import com.jonas.eurder.domain.ItemGroup;
import com.jonas.eurder.domain.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest {
    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 20);

    private final OrderRepository orderRepository = new OrderRepository();

    private final Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
    private final Item pen = new Item("Pen", "A blue pen", new BigDecimal("1.50"), 10);
    private final List<ItemGroup> itemGroups = List.of(
            new ItemGroup(book, 2, ORDER_DATE),
            new ItemGroup(pen, 4, ORDER_DATE));
    private final Order order = new Order("customer-id", itemGroups);

    @Test
    void givenSavedOrder_whenGetById_thenShouldReturnTheSameOrder() {
        orderRepository.save(order);

        assertThat(orderRepository.getById(order.getId())).contains(order);
    }

    @Test
    void getByCustomerId_returnsOnlyThatCustomersOrders() {
        Order ordersOfJonas1 = new Order("jonas-id", List.of(new ItemGroup(book, 1, ORDER_DATE)));
        Order ordersOfJonas2 = new Order("jonas-id", List.of(new ItemGroup(book, 2, ORDER_DATE)));
        Order orderOfBram = new Order("bram-id", List.of(new ItemGroup(book, 3, ORDER_DATE)));
        orderRepository.save(ordersOfJonas1);
        orderRepository.save(ordersOfJonas2);
        orderRepository.save(orderOfBram);

        assertThat(orderRepository.getByCustomerId("jonas-id"))
                .containsExactlyInAnyOrder(ordersOfJonas1, ordersOfJonas2);
    }

    @Test
    void getByCustomerId_givenCustomerWithoutOrders_returnsEmptyList() {
        orderRepository.save(new Order("bram-id", List.of(new ItemGroup(book, 1, ORDER_DATE))));

        assertThat(orderRepository.getByCustomerId("jonas-id")).isEmpty();
    }

}