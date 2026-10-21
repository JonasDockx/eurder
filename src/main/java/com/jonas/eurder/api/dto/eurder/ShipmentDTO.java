package com.jonas.eurder.api.dto.eurder;

import com.jonas.eurder.api.dto.address.AddressDTO;

import java.util.List;

public record ShipmentDTO(
        String orderId,
        AddressDTO shippingAddress,
        List<ShipmentItemGroupDTO> itemGroups
) {
}
