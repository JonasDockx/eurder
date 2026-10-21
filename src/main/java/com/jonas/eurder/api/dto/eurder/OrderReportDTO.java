package com.jonas.eurder.api.dto.eurder;

import java.math.BigDecimal;
import java.util.List;

public record OrderReportDTO(
        List<OrderInReportDTO> orders,
        BigDecimal totalPrice
) {
}
