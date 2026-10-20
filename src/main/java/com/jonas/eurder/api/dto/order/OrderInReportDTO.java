package com.jonas.eurder.api.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderInReportDTO(
        String orderId,
        List<ItemGroupInReportDTO> itemGroups,
        BigDecimal totalPrice
) {
}
