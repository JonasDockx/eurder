package com.jonas.eurder.api.dto.item;

import java.math.BigDecimal;

public record ItemDTO(
        String id,
        String name,
        String description,
        BigDecimal price,
        int amount
) {
}
