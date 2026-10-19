package com.jonas.eurder.domain;

import java.math.BigDecimal;
import java.util.UUID;

import static com.jonas.eurder.domain.Validation.*;

public class Item {
    private final String id;
    private final String name;
    private final String description;
    private BigDecimal price;
    private int amount;

    public Item(String name, String description, BigDecimal price, int amount) {
        this.id = UUID.randomUUID().toString();
        this.name = validateNotBlank(name, "Name");
        this.description = validateNotBlank(description, "Description");
        this.price = validateNotNegative(validateNotNull(price, "Price"), "Price");
        this.amount = validateNotNegative(validateNotNull(amount, "Amount"), "Amount");;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }
}
