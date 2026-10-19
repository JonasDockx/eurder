package com.jonas.eurder.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 19);

    @Test
    void totalPrice_isSumOfItemGroupTotals() {
        Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        Item pen = new Item("Pen", "A blue pen", new BigDecimal("1.50"), 10);
        List<ItemGroup> itemGroups = List.of(
                new ItemGroup(book, 2, ORDER_DATE),
                new ItemGroup(pen, 4, ORDER_DATE));

        Order order = new Order("customer-id", itemGroups);

        assertThat(order.getTotalPrice()).isEqualByComparingTo("35.98");
    }

    @Test
    void givenNoItemGroups_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Order("customer-id", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenNoCustomerId_thenThrowsIllegalArgumentException() {
        Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        Item pen = new Item("Pen", "A blue pen", new BigDecimal("1.50"), 10);
        List<ItemGroup> itemGroups = List.of(
                new ItemGroup(book, 2, ORDER_DATE),
                new ItemGroup(pen, 4, ORDER_DATE));

        assertThatThrownBy(() -> new Order(" ", itemGroups))
                .isInstanceOf(IllegalArgumentException.class);
    }
}