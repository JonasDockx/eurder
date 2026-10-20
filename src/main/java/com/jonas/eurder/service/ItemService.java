package com.jonas.eurder.service;

import com.jonas.eurder.api.dto.item.CreateItemDTO;
import com.jonas.eurder.api.dto.item.ItemDTO;
import com.jonas.eurder.api.dto.item.ItemMapper;
import com.jonas.eurder.domain.Item;
import com.jonas.eurder.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    public ItemDTO createItem(CreateItemDTO dto) {
        Item item = itemMapper.toItem(dto);
        itemRepository.save(item);
        logger.info("Item created with id {}", item.getId());
        return itemMapper.toDTO(item);
    }
}
