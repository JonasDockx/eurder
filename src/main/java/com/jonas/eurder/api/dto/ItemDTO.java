package com.jonas.eurder.api.dto;

import java.math.BigDecimal;

public record ItemDTO(
        String id,
        String name,
        String description,
        BigDecimal price,
        int amount
) {
}
