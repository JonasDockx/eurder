package com.jonas.eurder.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ItemGroupTest {

    private static final LocalDate ORDER_DATE = LocalDate.of(2026, 10, 19);

    @Test
    void givenEnoughStock_thenShippingDateIsNextDay() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        ItemGroup itemGroup = new ItemGroup(item, 2, ORDER_DATE);

        assertThat(itemGroup.getShippingDate()).isEqualTo(LocalDate.of(2026, 10, 20));
    }

    @Test
    void givenOrderedAmountEqualToStock_thenShippingDateIsNextDay() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        ItemGroup itemGroup = new ItemGroup(item, 5, ORDER_DATE);

        assertThat(itemGroup.getShippingDate()).isEqualTo(LocalDate.of(2026, 10, 20));
    }

    @Test
    void givenNotEnoughStock_thenShippingDateIsNextWeek() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 1);

        ItemGroup itemGroup = new ItemGroup(item, 5, ORDER_DATE);

        assertThat(itemGroup.getShippingDate()).isEqualTo(LocalDate.of(2026, 10, 26));
    }

    @Test
    void totalPrice_isUnitPriceTimesAmount() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        ItemGroup itemGroup = new ItemGroup(item, 3, ORDER_DATE);

        assertThat(itemGroup.getTotalPrice()).isEqualByComparingTo("44.97");
    }

    @Test
    void givenItem_thenCopiesItsIdNameAndPrice() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        ItemGroup itemGroup = new ItemGroup(item, 2, ORDER_DATE);

        assertThat(itemGroup.getItemId()).isEqualTo(item.getId());
        assertThat(itemGroup.getItemName()).isEqualTo("Book A");
        assertThat(itemGroup.getUnitPrice()).isEqualByComparingTo("14.99");
    }

    @Test
    void givenZeroAmount_thenThrowsIllegalArgumentException() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        assertThatThrownBy(() -> new ItemGroup(item, 0, ORDER_DATE))
                .isInstanceOf(IllegalArgumentException.class);
    }
}