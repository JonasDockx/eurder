package com.jonas.eurder.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void decreaseStock_givenMoreThanInStock_thenStockBecomesNegative() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 1);

        item.decreaseStock(3);

        assertThat(item.getAmount()).isEqualTo(-2);
    }

    @Test
    void update_givenValidData_thenAllFieldsChangeButIdStaysTheSame() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        String originalId = item.getId();

        item.update("Book B", "A short book", new BigDecimal("9.99"), 10);

        assertThat(item.getId()).isEqualTo(originalId);
        assertThat(item.getName()).isEqualTo("Book B");
        assertThat(item.getDescription()).isEqualTo("A short book");
        assertThat(item.getPrice()).isEqualByComparingTo("9.99");
        assertThat(item.getAmount()).isEqualTo(10);
    }

    @Test
    void update_GivenBlankDescription_thenThrowsIllegalArgumentException() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        assertThatThrownBy(() -> item.update("Book B", " ", new BigDecimal("9.99"), 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void update_givenInvalidPrice_thenItemIsNotChangedAtAll() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);

        assertThatThrownBy(() -> item.update("Book B", "A short book", new BigDecimal("-1"), 10))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(item.getName()).isEqualTo("Book A");
        assertThat(item.getPrice()).isEqualByComparingTo("14.99");
    }

    @Test
    void getUrgencyLevel_givenSixUnitsInStock_thenReturnsStockMedium() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 6);

        assertThat(item.getUrgencyLevel()).isEqualByComparingTo(UrgencyLevel.STOCK_MEDIUM);
    }

    @Test
    void getUrgencyLevel_givenNegativeFiveUnitsInStock_thenReturnsStockLow() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 1);
        item.decreaseStock(7);

        assertThat(item.getUrgencyLevel()).isEqualByComparingTo(UrgencyLevel.STOCK_LOW);
    }

    @Test
    void getUrgencyLevel_givenTwentyUnitsInStock_thenReturnsStockHigh() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 20);

        assertThat(item.getUrgencyLevel()).isEqualByComparingTo(UrgencyLevel.STOCK_HIGH);
    }
}