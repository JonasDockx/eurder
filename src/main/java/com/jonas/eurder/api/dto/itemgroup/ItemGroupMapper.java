package com.jonas.eurder.api.dto.itemgroup;

import com.jonas.eurder.domain.ItemGroup;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemGroupMapper {

    public ItemGroupDTO toDTO(ItemGroup itemGroup) {
        if (itemGroup == null) {
            return null;
        }

        return new ItemGroupDTO(
                itemGroup.getItemId(),
                itemGroup.getItemName(),
                itemGroup.getAmount(),
                itemGroup.getUnitPrice(),
                itemGroup.getShippingDate(),
                itemGroup.getTotalPrice()
        );
    }

    public List<ItemGroupDTO> toDTO(List<ItemGroup> itemGroups) {
        return itemGroups.stream()
                .map(this::toDTO)
                .toList();
    }
}
