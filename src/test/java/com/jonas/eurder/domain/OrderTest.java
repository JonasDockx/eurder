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

    @Test
    void belongsTo_givenTheCustomerWhoPlacedIt_thenReturnsTrue() {
        Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        Order order = new Order("jonas-id", List.of(new ItemGroup(book, 1, ORDER_DATE)));

        assertThat(order.belongsTo("jonas-id")).isTrue();
    }

    @Test
    void belongsTo_givenAnotherCustomer_thenReturnsFalse() {
        Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        Order order = new Order("jonas-id", List.of(new ItemGroup(book, 1, ORDER_DATE)));

        assertThat(order.belongsTo("bram-id")).isFalse();
    }

    @Test
    void getItemGroupsShippingOn_returnsOnlyGroupsShippingOnThatDate() {
        Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);   // in stock
        Item pen = new Item("Pen", "A blue pen", new BigDecimal("1.50"), 0);         // out of stock
        ItemGroup bookGroup = new ItemGroup(book, 1, ORDER_DATE);   // ships 2026-10-20
        ItemGroup penGroup = new ItemGroup(pen, 1, ORDER_DATE);     // ships 2026-10-26
        Order order = new Order("customer-id", List.of(bookGroup, penGroup));

        assertThat(order.getItemGroupsShippingOn(LocalDate.of(2026, 10, 20))).containsExactly(bookGroup);
        assertThat(order.getItemGroupsShippingOn(LocalDate.of(2026, 10, 26))).containsExactly(penGroup);
    }

    @Test
    void getItemGroupsShippingOn_givenDateWithoutShipments_returnsEmptyList() {
        Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        Order order = new Order("customer-id", List.of(new ItemGroup(book, 1, ORDER_DATE)));

        assertThat(order.getItemGroupsShippingOn(LocalDate.of(2026, 10, 21))).isEmpty();
    }
}