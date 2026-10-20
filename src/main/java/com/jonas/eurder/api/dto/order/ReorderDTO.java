package com.jonas.eurder.api.dto.order;

import jakarta.validation.constraints.NotBlank;

public record ReorderDTO(
        @NotBlank String customerId
) {
}
