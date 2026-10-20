package com.jonas.eurder.api.dto.customer;

import com.jonas.eurder.api.dto.address.AddressDTO;

public record CustomerDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        AddressDTO address,
        String phoneNumber
) {
}