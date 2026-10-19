package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderRepository {

    private final ConcurrentHashMap<String, Order> orders;

    public OrderRepository() {
        this.orders = new ConcurrentHashMap<>();
    }

    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    public Optional<Order> getById(String id) {
        return Optional.ofNullable(orders.get(id));
    }
}
