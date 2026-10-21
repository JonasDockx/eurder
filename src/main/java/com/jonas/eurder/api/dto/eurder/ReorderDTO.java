package com.jonas.eurder.api.dto.eurder;

import jakarta.validation.constraints.NotBlank;

public record ReorderDTO(
        @NotBlank String customerId
) {
}
