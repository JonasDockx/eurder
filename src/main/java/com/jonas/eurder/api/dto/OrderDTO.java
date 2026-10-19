package com.jonas.eurder.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
        String id,
        String customerId,
        List<ItemGroupDTO> itemGroups,
        BigDecimal totalPrice
) {
}
