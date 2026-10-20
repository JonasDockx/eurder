package com.jonas.eurder.api.dto.order;

import java.math.BigDecimal;

public record ItemGroupInReportDTO(
        String itemName,
        int amount,
        BigDecimal totalPrice
) {
}
