package com.jonas.eurder.domain;

import java.math.BigDecimal;
import java.util.List;

import static com.jonas.eurder.domain.Validation.validateNotNull;

public class OrderReport {

    private final List<Order> orders;

    public OrderReport(List<Order> orders) {
        this.orders = List.copyOf(validateNotNull(orders, "Orders"));
    }

    public List<Order> getOrders() {
        return orders;
    }

    public BigDecimal getTotalPrice() {
        return orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
