package com.jonas.eurder.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.jonas.eurder.domain.Validation.validateNotBlank;
import static com.jonas.eurder.domain.Validation.validateNotEmpty;

@Entity
@Table(name = "eurder")
public class Order {
    @Id
    private String id;
    private String customerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_group", joinColumns = @JoinColumn(name = "eurder_id"))
    private List<ItemGroup> itemGroups = new ArrayList<>();

    protected Order() {
    }

    public Order(String customerId, List<ItemGroup> itemGroups) {
        this.id = UUID.randomUUID().toString();
        this.customerId = validateNotBlank(customerId, "Customer Id");
        this.itemGroups = new ArrayList<>(validateNotEmpty(itemGroups, "Item groups"));
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<ItemGroup> getItemGroups() {
        return Collections.unmodifiableList(itemGroups);
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
