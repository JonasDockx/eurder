package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Item;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryTest {

    private final Item item = new Item("Laptop", "A Leovo Laptop", new BigDecimal("2999.99"), 5);

    private final ItemRepository itemRepository = new ItemRepository();

    @Test
    void givenSaveItem_whenGetById_thenShouldReturnTheSameItem() {
        itemRepository.save(item);

        assertThat(itemRepository.getById(item.getId())).contains(item);
    }
}