package com.jonas.eurder.api.dto.itemgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateItemGroupDTO(
        @NotBlank String itemId,
        @NotNull @Positive Integer amount
) {
}
