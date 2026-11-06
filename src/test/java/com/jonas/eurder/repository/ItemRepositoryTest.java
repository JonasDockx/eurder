package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final Item item = new Item("Laptop", "A Lenovo Laptop", new BigDecimal("2999.99"), 5);

    @Test
    void givenSaveItem_whenFindById_thenReturnsEqualCopyWithAllFields() {
        itemRepository.save(item);
        entityManager.flush();
        entityManager.clear();

        Item found = itemRepository.findById(item.getId()).orElseThrow();

        assertThat(found).isNotSameAs(item);
        assertThat(found).isEqualTo(item).hasSameHashCodeAs(item);
        assertThat(found).usingRecursiveComparison().isEqualTo(item);
    }
}