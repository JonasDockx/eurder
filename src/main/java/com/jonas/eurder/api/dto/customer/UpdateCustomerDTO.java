package com.jonas.eurder.api.dto.customer;

import com.jonas.eurder.api.dto.address.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotNull @Valid AddressDTO address,
        @NotBlank String phoneNumber
) {
}
