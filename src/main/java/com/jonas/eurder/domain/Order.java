package com.jonas.eurder.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.jonas.eurder.domain.Validation.validateNotBlank;
import static com.jonas.eurder.domain.Validation.validateNotEmpty;

public class Order {
    private final String id;
    private final String customerId;
    private final List<ItemGroup> itemGroups;

    public Order(String customerId, List<ItemGroup> itemGroups) {
        this.id = UUID.randomUUID().toString();
        this.customerId = validateNotBlank(customerId, "Customer Id");
        this.itemGroups = List.copyOf(validateNotEmpty(itemGroups, "Item groups"));
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    public BigDecimal getTotalPrice() {
        return itemGroups.stream()
                .map(ItemGroup::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean belongsTo(String customerId) {
        return this.customerId.equals(customerId);
    }

    public List<ItemGroup> getItemGroupsShippingOn(LocalDate date) {
        return itemGroups.stream()
                .filter(itemGroup -> itemGroup.getShippingDate().equals(date))
                .toList();
    }
}
