package com.jonas.eurder.api.dto.item;

import com.jonas.eurder.domain.UrgencyLevel;

import java.math.BigDecimal;

public record ItemWithStockLevelDTO(
        String id,
        String name,
        String description,
        BigDecimal price,
        int amount,
        UrgencyLevel urgencyLevel
) {
}
