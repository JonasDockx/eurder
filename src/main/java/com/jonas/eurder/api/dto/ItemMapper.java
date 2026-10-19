package com.jonas.eurder.api.dto;

import com.jonas.eurder.domain.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item toItem(CreateItemDTO dto) {
        return new Item(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.amount()
        );
    }

    public ItemDTO toDTO(Item item) {
        if (item == null) {
            return null;
        }

        return new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getAmount()
        );
    }
}
