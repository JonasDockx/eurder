package com.jonas.eurder.api.dto.order;

import com.jonas.eurder.api.dto.itemgroup.ItemGroupDTO;
import com.jonas.eurder.api.dto.itemgroup.ItemGroupMapper;
import com.jonas.eurder.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    private final ItemGroupMapper itemGroupMapper;

    public OrderMapper(ItemGroupMapper itemGroupMapper) {
        this.itemGroupMapper = itemGroupMapper;
    }

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        List<ItemGroupDTO> itemGroupDTOS = itemGroupMapper.toDTO(order.getItemGroups());

        return new OrderDTO(
                order.getId(),
                order.getCustomerId(),
                itemGroupDTOS,
                order.getTotalPrice()
        );
    }
}
