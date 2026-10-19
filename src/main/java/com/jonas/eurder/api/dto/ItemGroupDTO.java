package com.jonas.eurder.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ItemGroupDTO(
        String itemId,
        String itemName,
        int amount,
        BigDecimal unitPrice,
        LocalDate shippingDate,
        BigDecimal totalPrice
) {
}
