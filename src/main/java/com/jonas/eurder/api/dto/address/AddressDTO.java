package com.jonas.eurder.api.dto.address;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
        @NotBlank
        String street,

        @NotBlank
        String houseNumber,

        @NotBlank
        String postalCode,

        @NotBlank
        String city) {
}
