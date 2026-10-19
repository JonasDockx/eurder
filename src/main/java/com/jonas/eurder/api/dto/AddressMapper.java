package com.jonas.eurder.api.dto;

import com.jonas.eurder.domain.Address;

public class AddressMapper {

    public Address toAddress(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Address(
                dto.street(),
                dto.houseNumber(),
                dto.postalCode(),
                dto.city()
        );
    }

    public AddressDTO toAddressDTO(Address address) {
        return new AddressDTO(
                address.street(),
                address.houseNumber(),
                address.postalCode(),
                address.city()
        );
    }
}
