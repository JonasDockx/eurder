package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Customer;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository {

    private final ConcurrentHashMap<String, Customer> customers;

    public CustomerRepository() {
        this.customers = new ConcurrentHashMap<>();
    }

    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public Optional<Customer> getById(String id) {
        return customers.values().stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();
    }
}
