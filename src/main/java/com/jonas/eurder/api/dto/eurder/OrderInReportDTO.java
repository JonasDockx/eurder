package com.jonas.eurder.api.dto.eurder;

import java.math.BigDecimal;
import java.util.List;

public record OrderInReportDTO(
        String orderId,
        List<ItemGroupInReportDTO> itemGroups,
        BigDecimal totalPrice
) {
}
