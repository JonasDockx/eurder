package com.jonas.eurder.domain;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.jonas.eurder.domain.Validation.validatePositive;

@Embeddable
public class ItemGroup {

    private String itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private int amount;
    private LocalDate shippingDate;

    protected ItemGroup() {
    }

    public ItemGroup(Item item, int amount, LocalDate orderDate) {
        this.itemId = item.getId();
        this.itemName = item.getName();
        this.unitPrice = item.getPrice();
        this.amount = validatePositive(amount, "Amount");
        if (item.getAmount() >= amount) {
            this.shippingDate = orderDate.plusDays(1);
        } else {
            this.shippingDate = orderDate.plusDays(7);
        }
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(amount));
    }
}
