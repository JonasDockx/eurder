package com.jonas.eurder.api.dto.eurder;

import com.jonas.eurder.api.dto.itemgroup.CreateItemGroupDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderDTO(
        @NotEmpty List<@Valid CreateItemGroupDTO> itemGroups
) {
}
