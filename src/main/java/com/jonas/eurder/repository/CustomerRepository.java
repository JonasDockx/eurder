package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Customer;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CustomerRepository {

    private final ConcurrentHashMap<String, Customer> customers;

    public CustomerRepository() {
        this.customers = new ConcurrentHashMap<>();
    }

    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public Optional<Customer> getById(String id) {
        return Optional.ofNullable(customers.get(id));
    }
}
