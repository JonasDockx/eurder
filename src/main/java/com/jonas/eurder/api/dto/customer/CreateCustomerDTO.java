package com.jonas.eurder.api.dto.customer;

import com.jonas.eurder.api.dto.address.AddressDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCustomerDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull AddressDTO address,
        @NotBlank String phoneNumber
) {
}