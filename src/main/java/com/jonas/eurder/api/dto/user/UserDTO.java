package com.jonas.eurder.api.dto.user;

public record UserDTO(
        String email,
        String role,
        String customerId
) {
}
