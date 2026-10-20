package com.jonas.eurder.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderReportTest {

    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 20);

    @Test
    void totalPrice_isSumOfAllOrderTotals() {
        Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        Item pen = new Item("Pen", "A blue pen", new BigDecimal("1.50"), 10);
        Order firstOrder = new Order("customer-id", List.of(
                new ItemGroup(book, 2, ORDER_DATE),
                new ItemGroup(pen, 4, ORDER_DATE)));
        Order secondOrder = new Order("customer-id", List.of(
                new ItemGroup(book, 1, ORDER_DATE)));

        OrderReport report = new OrderReport(List.of(firstOrder, secondOrder));

        assertThat(report.getTotalPrice()).isEqualByComparingTo("50.97");
        assertThat(report.getOrders()).containsExactly(firstOrder, secondOrder);
    }

    @Test
    void givenNoOrders_thenTotalPriceIsZero() {
        OrderReport report = new OrderReport(List.of());

        assertThat(report.getTotalPrice()).isEqualByComparingTo("0");
        assertThat(report.getOrders()).isEmpty();
    }
}