package com.jonas.eurder.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.jonas.eurder.domain.Validation.validateNotBlank;

public class Order {
    private final String id;
    private final String customerId;
    private final List<ItemGroup> itemGroups;

    public Order(String customerId, List<ItemGroup> itemGroups) {
        this.id = UUID.randomUUID().toString();
        this.customerId = validateNotBlank(customerId, "Customer Id");
        if (itemGroups.isEmpty()) {
            throw new IllegalArgumentException("itemGroups list for an order cannot be empty");
        } else {
            this.itemGroups = List.copyOf(itemGroups);
        }
    }

    public BigDecimal getTotalPrice() {
        return itemGroups.stream()
                .map(ItemGroup::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
