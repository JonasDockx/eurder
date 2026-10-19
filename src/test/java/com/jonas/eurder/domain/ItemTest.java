package com.jonas.eurder.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void createItem_givenValidData_thenIdIsGeneratedAndFieldsAreSet() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo("Book A");
        assertThat(item.getPrice()).isEqualByComparingTo("14.99");
        assertThat(item.getAmount()).isEqualTo(5);
    }

    @Test
    void createItem_givenZetoStock_thenItemIsCreated() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 0);

        assertThat(item.getAmount()).isZero();
    }

    @Test
    void createItem_givenBlankName_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Item(" ", "A long book", new BigDecimal("14.99"), 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createItem_givenBlankDescription_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Item("Book A", " ", new BigDecimal("14.99"), 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createItem_givenNullPrice_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Item("Book A", "A long book", null, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createItem_givenNegativePrice_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Item("Book A", "A long book", new BigDecimal("-14.99"), 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createItem_givenNegativeStock_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Item("Book A", "A long book", new BigDecimal("14.99"), -5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void decreaseStock_givenEnoughStock_thenStockIsReduced() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        item.decreaseStock(2);

        assertThat(item.getAmount()).isEqualTo(3);
    }

    @Test
    void decreaseStock_givenMoreThanInStock_thenStockBecomesZero() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 1);

        item.decreaseStock(3);

        assertThat(item.getAmount()).isZero();
    }
}