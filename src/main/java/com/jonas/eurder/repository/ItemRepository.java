package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ItemRepository {

    private final ConcurrentHashMap<String, Item> items;

    public ItemRepository() {
        this.items = new ConcurrentHashMap<>();
    }

    public void save(Item item) {
        items.put(item.getId(), item);
    }

    public Optional<Item> getById(String id) {
        return Optional.ofNullable(items.get(id));
    }
}
