package com.jonas.eurder.api.dto.eurder;

import java.math.BigDecimal;

public record ItemGroupInReportDTO(
        String itemName,
        int amount,
        BigDecimal totalPrice
) {
}
