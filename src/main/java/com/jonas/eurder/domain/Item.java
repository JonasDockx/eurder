package com.jonas.eurder.domain;

import java.math.BigDecimal;
import java.util.UUID;

import static com.jonas.eurder.domain.Validation.*;

public class Item {
    private final String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int amount;

    public Item(String name, String description, BigDecimal price, int amount) {
        this.id = UUID.randomUUID().toString();
        this.name = validateNotBlank(name, "Name");
        this.description = validateNotBlank(description, "Description");
        this.price = validateNotNegative(validateNotNull(price, "Price"), "Price");
        this.amount = validateNotNegative(amount, "Amount");
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

    public void decreaseStock(int orderedAmount) {
        this.amount = amount - orderedAmount;
    }

    public void update(String name, String description, BigDecimal price, int amount) {
        String validatedName = validateNotBlank(name, "Name");
        String validatedDescription = validateNotBlank(description, "Description");
        BigDecimal validatedPrice = validateNotNegative(validateNotNull(price, "Price"), "Price");
        int validatedAmount = validateNotNegative(amount, "Amount");

        this.name = validatedName;
        this.description = validatedDescription;
        this.price = validatedPrice;
        this.amount = validatedAmount;
    }
}
