package com.jonas.eurder.api.dto;

public record CustomerDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        AddressDTO address,
        String phoneNumber
) {
}