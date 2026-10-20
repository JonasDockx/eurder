package com.jonas.eurder.api.dto.order;

import com.jonas.eurder.api.dto.itemgroup.ItemGroupDTO;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
        String id,
        String customerId,
        List<ItemGroupDTO> itemGroups,
        BigDecimal totalPrice
) {
}
